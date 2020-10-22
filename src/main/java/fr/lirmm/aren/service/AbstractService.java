package fr.lirmm.aren.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;

import fr.lirmm.aren.exception.InsertEntityException;
import fr.lirmm.aren.model.AbstractEntity;
import fr.lirmm.aren.producer.Scope;
import static fr.lirmm.aren.producer.Scope.Type.APPLICATION;
import static fr.lirmm.aren.producer.Scope.Type.REQUEST;
import java.util.Set;
import java.util.HashSet;
import javax.inject.Inject;

/**
 * Service that provides abstract operations for the models
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 * @param <T>
 */
public abstract class AbstractService<T extends AbstractEntity> {

    @Inject
    @Scope(REQUEST)
    private EntityManager emr;

    @Inject
    @Scope(APPLICATION)
    private EntityManager ema;

    private final Class<T> entityClass;

    /**
     *
     * @param type
     */
    public AbstractService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     *
     * @return
     */
    protected EntityManager getEntityManager() {
        try {
            this.emr.getTransaction();
            return emr;
        } catch (Exception e) {
            return this.ema;
        }
    }

    // Wrapper so if there is multiple nested requests
    // they are all commited once
    /**
     *
     */
    protected void transactionBegin() {
        if (!getEntityManager().getTransaction().isActive()) {
            getEntityManager().setProperty("activeTransactions", 0);
            getEntityManager().getTransaction().begin();
        }
        getEntityManager().setProperty("activeTransactions",
                ((int) getEntityManager().getProperties().get("activeTransactions")) + 1);
    }

    /**
     *
     */
    protected void commit() {
        if ((int) getEntityManager()
                .getProperties()
                .get("activeTransactions") == 1) {
            getEntityManager().getTransaction().commit();
        }
        getEntityManager().setProperty("activeTransactions",
                ((int) getEntityManager().getProperties().get("activeTransactions")) - 1);
    }

    /**
     *
     * @return
     */
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     *
     * @param entities
     */
    public void create(List<T> entities) {
        this.transactionBegin();
        entities.forEach(entity -> {
            this.create(entity);
        });
        this.commit();
    }

    /**
     *
     * @param entity
     */
    protected void afterCreate(T entity) {
    }

    /**
     *
     * @param entity
     */
    public void create(T entity) {
        // If ever the entity is attached, it needs to be detached
        getEntityManager().detach(entity);
        // If ever the id is set, it needs to be removed
        entity.setId(null);

        try {
            this.transactionBegin();
            getEntityManager().persist(entity);
            getEntityManager().refresh(entity);
            this.afterCreate(entity);
            this.commit();
        } catch (PersistenceException e) {
            if (e.getCause() instanceof PropertyValueException) {
                PropertyValueException cause = (PropertyValueException) e.getCause();
                throw InsertEntityException.MANDATORY_PROPERTY(cause.getPropertyName());
            } else if (e.getCause() instanceof ConstraintViolationException) {
                // Parse the error to a client readable one
                Throwable cause = e.getCause().getCause();
                String details = cause.getMessage();
                Pattern pattern = Pattern.compile("\\((.*)\\)=\\((.*)\\)");
                Matcher matcher = pattern.matcher(details);
                if (matcher.find()) {
                    String keyName = matcher.group(1);
                    String keyValue = matcher.group(2);
                    throw InsertEntityException.DUPLICATE_KEY(keyName, keyValue);
                } else {
                    throw InsertEntityException.OTHER(details);
                }
            } else {
                throw e;
            }
        }
    }

    /**
     *
     * @param entity
     */
    protected void afterEdit(T entity) {
    }

    /**
     *
     * @param entity
     */
    public void edit(T entity) {
        this.transactionBegin();
        getEntityManager().merge(entity);
        this.afterEdit(entity);
        this.commit();
        getEntityManager().refresh(entity);
    }

    /**
     *
     * @param entity
     */
    protected void afterRemove(T entity) {
    }

    /**
     *
     * @param entity
     */
    public void remove(T entity) {
        this.transactionBegin();
        getEntityManager().remove(getEntityManager().merge(entity));
        this.afterRemove(entity);
        this.commit();
    }

    /**
     *
     * @param id
     * @return
     */
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    /**
     *
     * @param id
     * @return
     */
    public T getReference(Long id) {
        return getEntityManager().getReference(entityClass, id);
    }

    /**
     *
     * @return
     */
    public Set<T> findAll() {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return new HashSet<T>(getEntityManager().createQuery(cq).getResultList());
    }

    /**
     *
     * @return
     */
    public int count() {
        CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder().createQuery(Long.class);
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
}
