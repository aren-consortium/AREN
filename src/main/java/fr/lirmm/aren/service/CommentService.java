package fr.lirmm.aren.service;

import java.util.List;

import javax.ws.rs.NotFoundException;

import fr.lirmm.aren.model.Comment;
import fr.lirmm.aren.model.TagSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.BiConsumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Service that provides operations for {link Comment}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class CommentService extends AbstractService<Comment> {

    @Inject
    private HttpRequestService httpRequestService;

    /**
     *
     */
    public CommentService() {
        super(Comment.class);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterCreate(Comment entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterRemove(Comment entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param commentId
     * @return
     */
    public Comment find(Long commentId) {
        List<Comment> results = getEntityManager().createQuery("SELECT c "
                + "FROM Comment c "
                + "WHERE c.id = :commentId ", Comment.class)
                .setParameter("commentId", commentId)
                .setMaxResults(1)
                .getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }

    /**
     *
     * @return
     */
    public Set<Comment> findAll() {
        return new HashSet<Comment>(getEntityManager().createQuery("SELECT c "
                + "FROM Comment c", Comment.class)
                .getResultList());
    }

    /**
     *
     * @param ids
     * @return
     */
    public Set<Comment> findAll(List<Long> ids) {
        return new HashSet<Comment>(getEntityManager().createQuery("SELECT c "
                + "FROM Comment c "
                + "WHERE id IN :ids", Comment.class)
                .setParameter("ids", ids)
                .getResultList());
    }

    /**
     *
     * @param comment
     */
    public void updateExternaleTables(Comment comment) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE Debate d SET "
                + "d.lastCommentDate = (SELECT MAX(c.created) FROM d.comments c), "
                + "d.commentsCount = (SELECT COUNT(c) FROM d.comments c), "
                + "d.commentsCountFor = (SELECT COUNT(c) FROM d.comments c WHERE c.opinion = :for), "
                + "d.commentsCountAgainst = (SELECT COUNT(c) FROM d.comments c WHERE c.opinion = :against) "
                + "WHERE d.id = :id")
                .setParameter("for", Comment.Opinion.FOR)
                .setParameter("against", Comment.Opinion.AGAINST)
                .setParameter("id", comment.getDebate().getId())
                .executeUpdate();
        getEntityManager().createQuery("UPDATE Category cat SET "
                + "cat.lastCommentDate = (SELECT MAX(c.created) FROM cat.documents doc LEFT JOIN doc.debates d LEFT JOIN d.comments c) "
                + "WHERE cat.id = :id")
                .setParameter("id", comment.getDebate().getDocument().getCategory().getId())
                .executeUpdate();
        super.commit();
    }

    private TagSet fetchNewTags(Comment comment, boolean fetchOnly) {
        TagSet tags = new TagSet();
        if (comment.getReformulation() != null && !comment.getReformulation().isEmpty()) {
            if (!fetchOnly && !comment.getProposedTags().isEmpty()) {
                tags = httpRequestService.sendTag(comment.getProposedTags(), comment.getReformulation());
            } else {
                tags = httpRequestService.retrieveTags(comment.getReformulation(), comment);
            }
        }
        return tags;
    }

    private TagSet fetchNewTags(Comment comment) {
        return fetchNewTags(comment, false);
    }

    /**
     *
     * @param comment
     * @param fetchOnly
     */
    public void updateTags(Comment comment, boolean fetchOnly) {
        TagSet tags = fetchNewTags(comment);
        getEntityManager().refresh(comment);
        comment.setTags(tags);
        comment.removeUnusedTags();
        this.edit(comment);
    }

    /**
     *
     * @param comment
     */
    public void updateTags(Comment comment) {
        updateTags(comment, false);
    }

    /**
     *
     * @param cms
     */
    public void updateAllTags(BiConsumer<Comment, Float> callback) {
        Set<Comment> comments = this.findAll();
        System.out.println("Starting tags update on " + comments.size() + " comments");
        int cpt = 0;
        Iterator<Comment> it = comments.iterator();
        while (it.hasNext()) {
            Comment comment = it.next();
            if (comment.getDebate().isIdfixLink()) {
                this.updateTags(comment, true);
            }
            if (callback != null) {
                callback.accept(comment, ((float) cpt / (float) comments.size()));
            }
            cpt++;
            System.out.print(".");
        }
        System.out.println();
        System.out.println("Tags update done");
    }

    /**
     *
     */
    public void updateAllTags() {
        this.updateAllTags(null);
    }
}
