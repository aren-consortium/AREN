package fr.lirmm.aren.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;

import fr.lirmm.aren.model.Document;
import java.util.Set;
import java.util.HashSet;

/**
 * Service that provides operations for {link Document}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class DocumentService extends AbstractService<Document> {

    /**
     *
     */
    public DocumentService() {
        super(Document.class);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterCreate(Document entity) {
        this.updateExternaleTables(entity);
    }

    /**
     *
     * @param entity
     */
    @Override
    protected void afterRemove(Document entity) {
        this.updateExternaleTables(entity);
    }

    private TypedQuery<Document> generateQuery(Long documentId, boolean withDebates) {
        TypedQuery<Document> query = getEntityManager().createQuery("SELECT do "
                + "FROM Document do "
                + (withDebates
                        ? "LEFT JOIN FETCH do.debates d "
                        : "")
                + (documentId != null
                        ? "WHERE do.id = :documentId "
                        : ""),
                Document.class);
        if (documentId != null) {
            query.setParameter("documentId", documentId);
        }
        return query;
    }

    /**
     *
     * @param debateId
     * @param withDebates
     * @return
     */
    public Document find(Long debateId, boolean withDebates) {
        List<Document> results = generateQuery(debateId, withDebates).getResultList();
        if (results.isEmpty()) {
            throw new NotFoundException();
        }
        return results.get(0);
    }

    /**
     *
     * @param withDebates
     * @return
     */
    public Set<Document> findAll(boolean withDebates) {
        return new HashSet<Document>(generateQuery(null, withDebates).getResultList());
    }

    /**
     *
     * @param document
     */
    public void updateExternaleTables(Document document) {
        super.transactionBegin();
        getEntityManager().createQuery("UPDATE Category c SET "
                + "c.documentsCount = (SELECT COUNT(doc) FROM c.documents doc) "
                + "WHERE c.id = :id")
                .setParameter("id", document.getCategory().getId())
                .executeUpdate();
        super.commit();
    }
}
