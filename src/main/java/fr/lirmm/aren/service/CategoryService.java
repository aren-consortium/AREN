package fr.lirmm.aren.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;

import fr.lirmm.aren.model.Category;
import fr.lirmm.aren.model.User;
import java.util.HashSet;

/**
 * Service that provides operations for {link Category}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class CategoryService extends AbstractService<Category> {

    /**
     *
     */
    public CategoryService() {
        super(Category.class);
    }

    private TypedQuery<Category> generateQuery(Long categoryId, User user, boolean withDocuments) {
        boolean isUser = user != null && user.is(User.Authority.USER) && !user.is(User.Authority.ADMIN);
        boolean onlyPublic = user != null && user.getAuthority().equals(User.Authority.GUEST);
        TypedQuery<Category> query = getEntityManager().createQuery("SELECT c "
                + "FROM Category c "
                + (withDocuments || isUser || onlyPublic
                        ? "LEFT JOIN " + (withDocuments ? "FETCH" : "") + " c.documents do "
                        : "")
                + (isUser || onlyPublic
                        ? "LEFT JOIN do.debates d "
                        : "")
                + (categoryId != null
                        ? "WHERE c.id = :categoryId "
                        : "WHERE c.id IS NOT NULL ")
                + (isUser
                        ? "AND (d.openPublic IS TRUE "
                        + "OR :user = d.owner "
                        + "OR :user IN (SELECT u FROM d.guests u) "
                        + "OR :user IN (SELECT u FROM d.teams t LEFT JOIN t.users u)) "
                        : (onlyPublic
                                ? "AND d.openPublic IS TRUE "
                                : "")),
                Category.class);
        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        if (isUser) {
            query.setParameter("user", user);
        }
        return query;
    }

    public Category findByUser(Long categoryId, User user, boolean withDocuments) {
        List<Category> results = generateQuery(categoryId, user, withDocuments).getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }

    public HashSet<Category> findAllByUser(User user, boolean withDocuments) {
        return new HashSet<Category>(generateQuery(null, user, withDocuments).getResultList());
    }

    /**
     *
     * @param categoryId
     * @return
     */
    public Category find(Long categoryId) {
        return findByUser(categoryId, null, false);
    }

    /**
     *
     * @return
     */
    public HashSet<Category> findAll() {
        return findAllByUser(null, false);
    }
}
