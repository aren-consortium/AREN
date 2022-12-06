package fr.lirmm.aren.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import fr.lirmm.aren.model.AbstractEntEntity;
import fr.lirmm.aren.model.Institution;
import fr.lirmm.aren.model.Team;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.aaf.AbstractRequest;
import fr.lirmm.aren.model.aaf.AddRequest;
import fr.lirmm.aren.model.aaf.AttributeList;
import fr.lirmm.aren.model.aaf.DeleteRequest;
import fr.lirmm.aren.model.aaf.FicAlimMENESR;
import fr.lirmm.aren.model.aaf.ModifyRequest;
import fr.lirmm.aren.security.PasswordEncoder;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Service that provides operations for AAF xml import
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class AAFImportService {

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private EntityManager em;

    /**
     * Object storing the parsed AAF xml file
     */
    private FicAlimMENESR alimAAF;

    /**
     * HashMap that links local ids to ent aaf ids of already stored entities
     */
    private final Map<Class<? extends AbstractEntEntity>, Map<String, Long>> maps = new HashMap<>();

    /**
     * HashMap the stores all the entities before the actual database processing
     * Useful to sort the in order of priority to avoid conflicts
     */
    private final Map<Class<? extends AbstractEntEntity>, Map<Method, List<AbstractEntEntity>>> toProcess = new HashMap<Class<? extends AbstractEntEntity>, Map<Method, List<AbstractEntEntity>>>();

    /**
     * Variables to evaluate the progression of the importation
     */
    private float amoutToProcess;
    private float amoutProceed;

    /**
     * Function triggered to notify the progression
     */
    private Consumer<Float> dispatcher;

    /**
     * Enum of possible requests
     */
    private enum Method {
        CREATE, UPDATE, DELETE;
    }

    /**
     * ArrayList that store the log of the import
     */
    private volatile List<String> log = new ArrayList<>();

    /**
     *
     * @return
     */
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     *
     * @return
     */
    public List<String> proceedImportation(File file) {
        return proceedImportation(file, true, true);
    }

    /**
     *
     * @param insertOnUpdate if true, insert elements that should be updated but
     * don't exist in local storage
     * @param updateOnInsert if true, update elements that should be inserted
     * but already exist in local storage
     * @return
     */
    public List<String> proceedImportation(File file, boolean insertOnUpdate, boolean updateOnInsert) {
        // Removes the check of external DTD on XML files
        System.setProperty("javax.xml.accessExternalDTD", "all");

        // Parse XML
        try {
            JAXBContext jaxbContext;
            jaxbContext = JAXBContext.newInstance(FicAlimMENESR.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            alimAAF = (FicAlimMENESR) unmarshaller.unmarshal(file);
        } catch (JAXBException ex) {
            Logger.getLogger(AAFImportService.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Varialbe sinitialization
        log.clear();

        toProcess.clear();

        for (Class<? extends AbstractEntEntity> klass : new Class[]{Institution.class, Team.class, User.class}) {
            toProcess.put(klass, new HashMap<Method, List<AbstractEntEntity>>());
            for (Method method : Method.values()) {
                toProcess.get(klass).put(method, new ArrayList<>());
            }
        }
        amoutToProcess = 0;
        amoutProceed = 0;

        // Count amout to proceed
        for (AbstractRequest request : alimAAF.getRequest()) {
            AttributeList opAttr = request.getOperationalAttributes();
            if (opAttr.isInstitution()) {
                AttributeList attrs = request.getAttributes();
                this.amoutToProcess += attrs.getClasses().size();
                this.amoutToProcess += attrs.getGroups().size();
            }
            this.amoutToProcess++;
        }
        this.amoutToProcess = this.amoutToProcess * 2;

        loadIdsMaps();

        // This populate the toProcess Map that will execute the real import
        Set<Long> newUsersId = new HashSet<Long>();
        for (AbstractRequest request : alimAAF.getRequest()) {
            AttributeList attrs = request.getAttributes();

            if (!attrs.isEmpty()) {
                String entId = request.getId();
                AttributeList opAttr = request.getOperationalAttributes();
                attrs.addAll(opAttr);
                Method method = null;
                if (request.getClass() == AddRequest.class) {
                    method = Method.CREATE;
                } else if (request.getClass() == ModifyRequest.class) {
                    method = Method.UPDATE;
                } else if (request.getClass() == DeleteRequest.class) {
                    method = Method.DELETE;
                }

                if (opAttr.isInstitution()) {
                    Institution institution = parseInstitution(attrs, entId);
                    toProcess.get(Institution.class).get(method).add(institution);
                    for (Team team : ((Institution) institution).getTeams()) {
                        toProcess.get(Team.class).get(method).add(team);
                        this.dispatchProgression();
                    }
                } else if (opAttr.isUser()) {
                    User user = parseUser(attrs, entId);
                    toProcess.get(User.class).get(method).add(user);
                }
                this.dispatchProgression();
            }
        }

        // This executes the real import
        // The order is important to avoid foreign key error
        getEntityManager().getTransaction().begin();
        for (Class<? extends AbstractEntEntity> klass : new Class[]{Institution.class, Team.class, User.class}) {
            // The order is important too
            for (Method method : new Method[]{Method.CREATE, Method.UPDATE, Method.DELETE}) {
                for (AbstractEntEntity entity : toProcess.get(klass).get(method)) {
                    proceed(klass, method, entity, insertOnUpdate, updateOnInsert);
                    if (klass == User.class) {
                        newUsersId.add(entity.getId());
                    }
                    this.dispatchProgression();
                }
            }
        }
        if (!newUsersId.isEmpty()) {
            getEntityManager().createQuery("UPDATE Team t SET "
                    + "t.usersCount = (SELECT COUNT(u) FROM t.users u) "
                    + "WHERE t IN (SELECT t1 FROM User u LEFT JOIN u.teams t1 WHERE u.id IN :ids)")
                    .setParameter("ids", newUsersId)
                    .executeUpdate();
        }
        getEntityManager().getTransaction().commit();

        return log;
    }

    /**
     *
     * @param dispatcher
     */
    public void setDispatcher(Consumer<Float> dispatcher) {

        this.dispatcher = dispatcher;
    }

    /**
     *
     * @param message
     * @param isTeam if true it increments the total amout to process, because
     * team ar
     */
    private void dispatchProgression() {

        this.amoutProceed++;
        if (this.dispatcher != null) {
            float progression = this.amoutProceed / this.amoutToProcess;
            this.dispatcher.accept(progression);
        }
    }

    /**
     * Populate the maps between ids and ent ids
     */
    private void loadIdsMaps() {

        maps.put(User.class, getEntIdToIdMap(User.class));
        maps.put(Team.class, getEntIdToIdMap(Team.class));
        maps.put(Institution.class, getEntIdToIdMap(Institution.class));
    }

    /**
     * Retreives all ids and ent ids of a certain type in one call
     *
     * @param klass
     * @return
     */
    private Map<String, Long> getEntIdToIdMap(Class<? extends AbstractEntEntity> klass) {
        Map<String, Long> map = new HashMap<String, Long>();
        List<Object[]> result = getEntityManager().createQuery(
                "SELECT t.entId, t.id "
                + "FROM " + klass.getSimpleName() + " t "
                + "WHERE t.entId IS NOT NULL", Object[].class)
                .getResultList();
        if (result == null) {
            return null;
        }

        result.forEach(obj -> {
            map.put((String) obj[0], (Long) obj[1]);
        });

        return map;
    }

    /**
     *
     * This is the main logic It handles what needs to be done for Create,
     * Update and Delete
     *
     * @param method
     * @param entity
     * @param entId
     */
    private void proceed(Class entityClass, Method method, AbstractEntEntity entity, boolean insertOnUpdate, boolean updateOnInsert) {

        boolean alreadyExists = maps.get(entityClass).containsKey(entity.getEntId());

        switch (method) {
            case CREATE:
                if (alreadyExists) {
                    if (updateOnInsert) {
                        // If the entity already exists and updateOnInsert is true, we update it
                        // And add a warning
                        log.add("WARNING : " + method.name() + " : " + entityClass.getSimpleName() + " : " + entity.getEntId() + " updated instead of created");
                        proceed(entityClass, Method.UPDATE, entity, false, false);
                    } else {
                        // If the entity already exists it adds an error
                        log.add("ERROR : " + method.name() + " : " + entityClass.getSimpleName() + " : " + entity.getEntId() + " already exists");
                    }
                } else {
                    getEntityManager().persist(entity);
                    log.add("SUCCESS : " + method.name() + " : " + entityClass.getSimpleName() + " : " + entity.getEntId());
                }
                break;

            case UPDATE:
                if (!alreadyExists) {
                    if (insertOnUpdate) {
                        // If the entity is not found and insertOnUpdate is true, we insert it
                        // And add a warning
                        log.add("WARNING : " + method.name() + " : " + entityClass.getSimpleName() + " : " + entity.getEntId() + " created instead of updated.");
                        proceed(entityClass, Method.CREATE, entity, false, false);
                    } else {
                        // If the entity is not found it adds an error
                        log.add("ERROR : " + method.name() + " : " + entityClass.getSimpleName() + " : " + entity.getEntId() + " not found.");
                    }
                } else {
                    getEntityManager().merge(entity);
                    log.add("SUCCESS : " + method.name() + " : " + entityClass.getSimpleName() + " : " + entity.getEntId());
                }
                break;

            case DELETE:
                if (!alreadyExists) {
                    log.add("ERROR : " + method.name() + " : " + entityClass.getSimpleName() + " : " + entity.getEntId() + " not found.");
                } else {
                    getEntityManager().remove(entity);
                    log.add("SUCCESS : " + method.name() + " : " + entityClass.getSimpleName() + " : " + entity.getEntId());
                }
                break;

            default:
                break;
        }
    }

    private User parseUser(AttributeList attrs, String entId) {

        Long id = maps.get(User.class).get(entId);
        User user = new User();
        if (id != null) {
            user = getEntityManager().getReference(User.class, id);
        }
        user.setEntId(entId);
        user.setLastName(attrs.getLastName());
        user.setFirstName(attrs.getFirstName());
        user.setEmail(attrs.getEmail());
        user.setAuthority(attrs.getAuthority());
        // TODO : Get the real username
        user.setUsername(entId);
        String hashedPassword = passwordEncoder.hashPassword("temporary");
        user.setPassword(hashedPassword);

        Long instId = maps.get(Institution.class).get(attrs.getInstitutionId());
        Institution inst = getEntityManager().getReference(Institution.class, instId);
        user.setInstitution(inst);

        List<String> classOrGroup = new ArrayList<>();
        classOrGroup.addAll(attrs.getClasses());
        classOrGroup.addAll(attrs.getGroups());

        for (String teamEntId : classOrGroup) {
            Long teamId = maps.get(Team.class).get(teamEntId);
            if (teamId != null) {
                Team team = getEntityManager().getReference(Team.class, teamId);
                user.addTeam(team);
            }
        }

        return user;
    }

    private Institution parseInstitution(AttributeList attrs, String entId) {

        Long id = maps.get(Institution.class).get(entId);
        Institution inst = new Institution();
        if (id != null) {
            inst = getEntityManager().getReference(Institution.class, id);
        }
        inst.setEntId(entId);

        inst.setAcademy(attrs.getAcademy());
        inst.setType(attrs.getType());
        inst.setName(attrs.getName().split("-")[1]);

        List<String> classOrGroup = new ArrayList<>();
        classOrGroup.addAll(attrs.getClasses());
        classOrGroup.addAll(attrs.getGroups());

        for (String teamDesc : classOrGroup) {
            if (!teamDesc.isEmpty()) {
                Team team = parseTeam(teamDesc, inst);
                inst.addTeam(team);
            }
        }

        return inst;
    }

    private Team parseTeam(String value, Institution inst) {

        String[] explodeName = value.split("\\$");
        String entId = inst.getEntId() + "$" + explodeName[0];
        String name = explodeName.length > 2 && explodeName[1].length() > 1 ? explodeName[1] : explodeName[0];

        Long id = maps.get(Team.class).get(entId);
        Team team = new Team();
        if (id != null) {
            team = getEntityManager().getReference(Team.class, id);
        }
        team.setEntId(entId);
        team.setName(name);

        return team;
    }
}
