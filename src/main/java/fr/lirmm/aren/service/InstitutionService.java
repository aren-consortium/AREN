package fr.lirmm.aren.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;

import fr.lirmm.aren.model.Institution;
import java.util.Set;
import java.util.HashSet;

/**
 * Service that provides operations for {link Institution}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class InstitutionService extends AbstractService<Institution> {

    /**
     *
     */
    public InstitutionService() {
        super(Institution.class);
    }

    /**
     *
     * @param entId
     * @return
     */
    public Institution findByEntId(String entId) {
        List<Institution> results = getEntityManager().createQuery("SELECT i "
                + "FROM Institution i "
                + "WHERE i.entId = :entId", Institution.class)
                .setParameter("entId", entId)
                .getResultList();

        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }

    /**
     *
     * @param withTeam
     * @param withUsers
     * @return
     */
    public Set<Institution> findAll(boolean withTeam, boolean withUsers) {
        return new HashSet<Institution>(getEntityManager().createQuery("SELECT i "
                + "FROM Institution i "
                + (withTeam || withUsers
                        ? "LEFT JOIN FETCH i.users u "
                        : "")
                + (withUsers
                        ? "LEFT JOIN FETCH i.teams t"
                        : ""), Institution.class)
                .getResultList());
    }
}
