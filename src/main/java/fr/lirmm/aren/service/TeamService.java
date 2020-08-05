package fr.lirmm.aren.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;

import fr.lirmm.aren.model.Team;
import java.util.Set;
import java.util.HashSet;

/**
 * Service that provides operations for {link Team}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class TeamService extends AbstractService<Team> {

    /**
     *
     */
    public TeamService() {
        super(Team.class);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterEdit(Team entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param standalone
     * @return
     */
    public Set<Team> findAll(boolean standalone) {

        return new HashSet<Team>(getEntityManager().createQuery("SELECT t "
                + "FROM Team t "
                + (standalone
                        ? "WHERE t.institution IS NULL"
                        : ""), Team.class)
                .getResultList());
    }

    /**
     *
     * @param entId
     * @return
     */
    public Team findByEntId(String entId) {
        List<Team> results = getEntityManager().createQuery("SELECT i "
                + "FROM Team t "
                + "WHERE t.entId = :entId", Team.class)
                .setParameter("entId", entId)
                .getResultList();

        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }

    /**
     *
     * @param team
     */
    public void updateExternaleTables(Team team) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE Team t SET "
                + "t.usersCount = (SELECT COUNT(u) FROM t.users u), "
                + "t.debatesCount = (SELECT COUNT(d) FROM t.debates d) "
                + "WHERE t.id = :id")
                .setParameter("id", team.getId())
                .executeUpdate();
        super.commit();
    }
}
