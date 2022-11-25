package fr.lirmm.aren.ws.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import fr.lirmm.aren.service.AbstractService;
import fr.lirmm.aren.exception.AccessDeniedException;
import fr.lirmm.aren.model.AbstractDatedEntity;
import fr.lirmm.aren.model.AbstractEntEntity;
import fr.lirmm.aren.model.AbstractEntity;
import fr.lirmm.aren.model.AbstractOwnedEntity;
import fr.lirmm.aren.model.Notification;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.security.AuthenticatedUserDetails;

import java.time.ZonedDateTime;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

/**
 * JAX-RS resource class for Abstract Model objects
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 * @param <T>
 */


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractRESTFacade<T extends AbstractEntity> {

    /**
     *
     */
    @Context
    protected SecurityContext securityContext;

    /**
     *
     */
    @Context
    protected HttpServletRequest request;

    /**
     *
     */
    @QueryParam("overview")
    protected String overview;

    /**
     *
     * @return
     */
    protected abstract AbstractService<T> getService();

    /**
     *
     * @return the authentificated user
     */
    protected User getUser() {
        return ((AuthenticatedUserDetails) securityContext.getUserPrincipal()).getUser();
    }

    /**
     *
     * @param entity
     */
    protected void safetyPreInsertion(T entity) {
        if (entity instanceof AbstractOwnedEntity && !(entity instanceof Notification)) {
            if (!getUser().is("SUPERADMIN") || ((AbstractOwnedEntity) entity).getOwner() == null) {
                ((AbstractOwnedEntity) entity).setOwner(getUser());
            }
        }
        if (entity instanceof AbstractDatedEntity) {
            if (!getUser().is("SUPERADMIN") || ((AbstractDatedEntity) entity).getCreated() == null) {
                ((AbstractDatedEntity) entity).setCreated(ZonedDateTime.now());
            }
        }
        if (entity instanceof AbstractEntEntity) {
            if (!getUser().is("SUPERADMIN") || ((AbstractEntEntity) entity).getEntId() == null) {
                ((AbstractEntEntity) entity).setEntId(null);
            }
        }
    }

    /**
     *
     * @param entity
     * @param entityToEdit
     */
    protected void safetyPreEdition(T entity, T entityToEdit) {
        if (!getUser().is("SUPERADMIN")) {
            if (!entityToEdit.isEditable()) {
                throw AccessDeniedException.UNMUTABLE_OBJECT(entity.getClass(), entityToEdit.getId());
            }
            if (entity instanceof AbstractOwnedEntity && !(entity instanceof Notification)) {
                ((AbstractOwnedEntity) entity).setOwner(null);
            }
            if (entity instanceof AbstractDatedEntity) {
                ((AbstractDatedEntity) entity).setCreated(null);
            }
            if (entity instanceof AbstractEntEntity) {
                ((AbstractEntEntity) entity).setEntId(null);
            }
        }
    }

    /**
     *
     * @param entity
     */
    protected void safetyPreDeletion(T entity) {
        if (!getUser().is("SUPERADMIN")) {
            if (!entity.isRemovable()) {
                throw AccessDeniedException.UNERASABLE_OBJECT(entity.getClass(), entity.getId());
            }
        }
    }

    /**
     * Create a new Entity
     *
     * @param entity to create
     * @return then create Entity
     */
    @POST
    @RolesAllowed({"SUPERADMIN"})
    public T create(T entity) {
        safetyPreInsertion(entity);
        getService().create(entity);
        return entity;
    }

    /**
     * Edit the Entity fetch by the first parameter with the content of the
     * second parameter. Null fields are ignored
     *
     * @param id of the Entity to update
     * @param entity values to update
     * @return the updated Entity
     */
    @PUT
    @Path("{id}")
    @RolesAllowed({"SUPERADMIN"})
    public T edit(@PathParam("id") Long id, T entity) {
        T entityToEdit = find(id);
        safetyPreEdition(entity, entityToEdit);
        entityToEdit.merge(entity);
        getService().edit(entityToEdit);
        return entityToEdit;
    }

    /**
     * Remove the Entity fetched by the first parameter
     *
     * @param id of the Entity to remove
     */
    @DELETE
    @Path("{id}")
    @RolesAllowed({"SUPERADMIN"})
    public void remove(@PathParam("id") Long id) {
        T entity = find(id);
        safetyPreDeletion(entity);
        getService().remove(entity);
    }

    /**
     * Fetch the Entity by id
     *
     * @param id of the Entity to fetch
     * @return an Entity
     */
    @GET
    @Path("{id}")
    @RolesAllowed({"SUPERADMIN"})
    public T find(@PathParam("id") Long id) {

        return getService().find(id);
    }

    /**
     * Fetch all the entity of the givent type
     *
     * @return a set of entity
     */
    @GET
    @RolesAllowed({"SUPERADMIN"})
    public Set<T> findAll() {

        return getService().findAll();
    }
}
