package fr.lirmm.aren.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;

import fr.lirmm.aren.model.Debate;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.User.Authority;
import java.util.Set;
import java.util.HashSet;

/**
 * Service that provides operations for {link Debate}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class DebateService extends AbstractService<Debate> {

    /**
     *
     */
    public DebateService() {
        super(Debate.class);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterCreate(Debate entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterRemove(Debate entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterEdit(Debate entity) {
        this.updateExternaleTables(entity);
    }

    private TypedQuery<Debate> generateQuery(Long debateId, User user, Long categoryId, boolean withDocument, boolean withComments, boolean withTeams, boolean withGuests, boolean withUsers) {
        boolean isUser = user != null && user.is(Authority.USER) && !user.is(Authority.ADMIN);
        boolean onlyPublic = user != null && user.getAuthority().equals(Authority.GUEST);
        TypedQuery<Debate> query = getEntityManager().createQuery("SELECT d "
                + "FROM Debate d "
                + (withComments
                        ? "LEFT JOIN FETCH d.comments c "
                        : "")
                + (withTeams || withUsers || isUser
                        ? "LEFT JOIN " + (withTeams || withUsers ? "FETCH" : "") + " d.teams t "
                        : "")
                + (withUsers || isUser
                        ? "LEFT JOIN " + (withUsers ? "FETCH" : "") + " t.users u "
                        : "")
                + (withGuests || isUser
                        ? "LEFT JOIN " + (withGuests ? "FETCH" : "") + " d.guests g "
                        : "")
                + (withDocument
                        ? "LEFT JOIN FETCH d.document do "
                        : "")
                + (debateId != null
                        ? "WHERE d.id = :debateId "
                        : "WHERE d.id IS NOT NULL ")
                + (categoryId != null
                        ? "AND d.document.category.id = :categoryId "
                        : "")
                + (isUser
                        ? "AND (d.openPublic IS TRUE "
                        + "OR :user = d.owner "
                        + "OR :user IN g "
                        + "OR :user IN u) "
                        : (onlyPublic
                                ? "AND d.openPublic IS TRUE "
                                : "")),
                Debate.class
        );

        if (debateId != null) {
            query.setParameter("debateId", debateId);
        }
        if (isUser) {
            query.setParameter("user", user);
        }
        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        return query;
    }

    /**
     *
     * @param debateId
     * @param user
     * @param withDocument
     * @param withComments
     * @param withTeams
     * @param withGuests
     * @param withUsers
     * @return
     */
    public Debate findByUser(Long debateId, User user, boolean withDocument, boolean withComments, boolean withTeams, boolean withGuests, boolean withUsers) {
        List<Debate> results = generateQuery(debateId, user, null, withDocument, withComments, withTeams, withGuests, withUsers).getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }

    /**
     *
     * @param user
     * @param categoryId
     * @param withDocument
     * @param withComments
     * @param withTeams
     * @param withGuests
     * @param withUsers
     * @return
     */
    public Set<Debate> findAllByUser(User user, Long categoryId, boolean withDocument, boolean withComments, boolean withTeams, boolean withGuests, boolean withUsers) {
        return new HashSet<Debate>(generateQuery(null, user, categoryId, withDocument, withComments, withTeams, withGuests, withUsers).getResultList());
    }

    /**
     *
     * @param debateId
     * @param withDocument
     * @param withComments
     * @param withTeams
     * @param withGuests
     * @param withUsers
     * @return
     */
    public Debate find(Long debateId, boolean withDocument, boolean withComments, boolean withTeams, boolean withGuests, boolean withUsers) {
        return findByUser(debateId, null, withDocument, withComments, withTeams, withGuests, withUsers);
    }

    /**
     *
     * @param categoryId
     * @param withDocument
     * @param withComments
     * @param withTeams
     * @param withGuests
     * @param withUsers
     * @return
     */
    public Set<Debate> findAll(Long categoryId, boolean withDocument, boolean withComments, boolean withTeams, boolean withGuests, boolean withUsers) {
        return findAllByUser(null, categoryId, withDocument, withComments, withTeams, withGuests, withUsers);
    }

    /**
     *
     * @param debateId
     */
    public void clearComments(Long debateId) {
        this.transactionBegin();
        getEntityManager().createQuery("DELETE FROM Comment c "
                + "WHERE c.debate.id = :debateId")
                .setParameter("debateId", debateId)
                .executeUpdate();
        getEntityManager().createQuery("UPDATE Debate d SET "
                + "d.commentsCount = 0, "
                + "d.commentsCountFor = 0, "
                + "d.commentsCountAgainst = 0, "
                + "d.lastCommentDate = null "
                + "WHERE d.id = :debateId")
                .setParameter("debateId", debateId)
                .executeUpdate();
        this.commit();
    }

    /**
     *
     * @param debate
     */
    public void updateExternaleTables(Debate debate) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE Category c SET "
                + "c.debatesCount = (SELECT COUNT(d) FROM c.documents doc LEFT JOIN doc.debates d) "
                + "WHERE c.id = :id")
                .setParameter("id", debate.getDocument().getCategory().getId())
                .executeUpdate();
        getEntityManager().createQuery("UPDATE Document doc SET "
                + "doc.debatesCount = (SELECT COUNT(d) FROM doc.debates d) "
                + "WHERE doc.id = :id")
                .setParameter("id", debate.getDocument().getId())
                .executeUpdate();
        getEntityManager().createQuery("UPDATE Team t SET "
                + "t.debatesCount = (SELECT COUNT(d) from t.debates d) "
                + "WHERE t IN (SELECT t1 FROM Debate d LEFT JOIN d.teams t1 WHERE d.id = :id)")
                .setParameter("id", debate.getId())
                .executeUpdate();
        super.commit();
    }
}
