package fr.lirmm.aren.ws.rest;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import fr.lirmm.aren.service.NotificationService;
import fr.lirmm.aren.model.Notification;
import java.util.Set;
import javax.ws.rs.QueryParam;

/**
 * JAX-RS resource class for Notifications managment
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("notifications")
public class NotificationRESTFacade extends AbstractRESTFacade<Notification> {

    @Inject
    private NotificationService notificationService;

    /**
     *
     */
    @QueryParam("overview")
    protected String overview;

    /**
     *
     * @return
     */
    @Override
    protected NotificationService getService() {
        return notificationService;
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"USER"})
    public Set<Notification> findAll() {
        if (overview == null) {
            return notificationService.findAllByUser(this.getUser().getId());
        } else {
            return notificationService.findAllFirstsByUser(this.getUser().getId(), 10);
        }
    }

    /**
     * Mark all Notifications of the current User as read
     *
     * @return
     */
    @PUT
    @RolesAllowed({"USER"})
    public Set<Notification> readAll() {
        notificationService.readAllByUser(this.getUser().getId());
        return notificationService.findAllFirstsByUser(this.getUser().getId(), 10);
    }

    /**
     *
     * @param id
     * @param notification
     */
    @Override
    @RolesAllowed({"USER"})
    public Notification edit(Long id, Notification notification) {
        Notification entityToUpdate;
        if (this.getUser().is("SUPERADMIN")) {
            notification.setContent(null);
            entityToUpdate = super.edit(id, notification);
        } else {
            entityToUpdate = notificationService.find(id);
            entityToUpdate.setUnread(notification.isUnread());
            notificationService.edit(entityToUpdate);
        }
        return entityToUpdate;
    }

}
