package fr.lirmm.aren.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fr.lirmm.aren.model.User;
import fr.lirmm.aren.security.PasswordEncoder;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.HashSet;

/**
 * Service that provides operations for {@link User}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class UserService extends AbstractService<User> {

    @Inject
    private PasswordEncoder passwordEncoder;

    /**
     *
     */
    public UserService() {
        super(User.class);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterEdit(User entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param user
     */
    @Override
    public void create(User user) {
        String hashedPassword = passwordEncoder.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        super.create(user);
    }

    /**
     *
     * @param user
     */
    @Override
    public void edit(User user) {
        super.edit(user);
    }

    /**
     *
     * @param user
     * @param newPassword
     */
    public void changePassword(User user, String newPassword) {
        String hashedPassword = passwordEncoder.hashPassword(newPassword);
        user.setPassword(hashedPassword);
        super.edit(user);
        this.invalidateToken(user);
    }

    /**
     * Find a user by username or email.
     *
     * @param identifier
     * @return
     */
    public User findByUsernameOrEmail(String identifier) {
        List<User> results = getEntityManager().createQuery("SELECT u "
                + "FROM User u "
                + "WHERE (u.username = :identifier "
                + "OR u.email = :identifier)", User.class)
                .setParameter("identifier", identifier)
                .getResultList();

        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    /**
     *
     * @param entId
     * @return
     */
    public User findByEntId(String entId) {
        List<User> results = getEntityManager().createQuery("SELECT u "
                + "FROM User u "
                + "WHERE u.active = true "
                + "AND u.entId = :entId", User.class)
                .setParameter("entId", entId)
                .getResultList();

        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    /**
     *
     * @param standalone
     * @return
     */
    public Set<User> findAll(boolean standalone) {

        return new HashSet<User>(getEntityManager().createQuery("SELECT u "
                + "FROM User u "
                + "WHERE u.active = true "
                + (standalone
                        ? "AND u.institution IS NULL "
                        : ""), User.class)
                .getResultList());
    }

    /**
     *
     * @param debateId
     * @return
     */
    public List<User> findByDebate(Long debateId) {
        List<User> users = getEntityManager().createQuery("SELECT u "
                + "FROM User u "
                + "LEFT JOIN u.teams t "
                + "LEFT JOIN t.debates d "
                + "WHERE u.active = true "
                + "AND d.id = :debateId", User.class)
                .setParameter("debateId", debateId)
                .getResultList();

        if (users.isEmpty()) {
            return null;
        }
        return users;
    }

    /**
     *
     * @param user
     */
    public void updateLastLogin(User user) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE User u "
                + "SET u.lastLogin = :now "
                + "WHERE u.id = :userId")
                .setParameter("now", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .setParameter("userId", user.getId())
                .executeUpdate();
        super.commit();
        getEntityManager().refresh(user);
    }

    /**
     *
     * @param user
     */
    public void invalidateToken(User user) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE User u "
                + "SET u.tokenValidity = :now "
                + "WHERE u.id = :userId")
                .setParameter("now", ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .setParameter("userId", user.getId())
                .executeUpdate();
        super.commit();
        getEntityManager().refresh(user);
    }

    /**
     *
     * @param userId
     */
    public void hide(Long userId) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE User u "
                + "SET u.active = :active "
                + ", u.password = NULL "
                + ", u.username = NULL "
                + ", u.firstName = NULL "
                + ", u.lastName = NULL "
                + ", u.email = NULL "
                + ", u.authority = :authority "
                + "WHERE u.id = :userId")
                .setParameter("authority", User.Authority.DELETED)
                .setParameter("active", false)
                .setParameter("userId", userId)
                .executeUpdate();
        super.commit();
    }

    /**
     *
     * @param user
     */
    public void updateExternaleTables(User user) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE Team t SET "
                + "t.usersCount = (SELECT COUNT(u) from t.users u) "
                + "WHERE t IN (SELECT t1 FROM User u LEFT JOIN u.teams t1 WHERE u.id = :id)")
                .setParameter("id", user.getId())
                .executeUpdate();
        super.commit();
    }
}
