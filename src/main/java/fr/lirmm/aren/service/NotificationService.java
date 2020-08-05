package fr.lirmm.aren.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import fr.lirmm.aren.model.Notification;
import java.util.Set;
import java.util.HashSet;

/**
 * Service that provides operations for {link Notification}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class NotificationService extends AbstractService<Notification> {

    /**
     *
     */
    public NotificationService() {
        super(Notification.class);
    }

    /**
     *
     * @param userId
     * @return
     */
    public Set<Notification> findAllByUser(Long userId) {

        List<Notification> notifs = getEntityManager().createQuery("SELECT n "
                + "FROM Notification n "
                + "WHERE n.owner.id = :userId "
                + "ORDER BY n.created DESC", Notification.class)
                .setParameter("userId", userId)
                .getResultList();
        if (notifs.isEmpty()) {
            return null;
        }
        return new HashSet<Notification>(notifs);
    }

    /**
     *
     * @param userId
     * @param readenLimit
     * @return
     */
    public Set<Notification> findAllFirstsByUser(Long userId, int readenLimit) {
        return new HashSet<Notification>(getEntityManager().createQuery("SELECT n1 "
                + "FROM Notification n1 "
                + "WHERE n1.owner.id = :userId "
                + "AND (n1.unread = true "
                + "OR (SELECT COUNT(id) "
                + "    FROM Notification n2 "
                + "    WHERE n2.owner.id = :userId "
                + "    AND n2.created >= n1.created) <= :readenLimit)")
                .setParameter("userId", userId)
                .setParameter("readenLimit", Long.valueOf(readenLimit))
                .getResultList());
    }

    /**
     *
     * @param userId
     */
    public void readAllByUser(Long userId) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE Notification n "
                + "SET n.unread = false "
                + "WHERE n.owner.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        super.commit();
    }

    /**
     *
     * @param userId
     */
    public void removeAllByUser(Long userId) {
        super.transactionBegin();
        getEntityManager().createQuery("DELETE Notification n "
                + "WHERE n.owner.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
        super.commit();
    }
}
