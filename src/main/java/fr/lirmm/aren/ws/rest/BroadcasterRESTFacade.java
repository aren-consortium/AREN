package fr.lirmm.aren.ws.rest;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import fr.lirmm.aren.service.BroadcasterService;
import fr.lirmm.aren.model.Debate;
import fr.lirmm.aren.model.Notification;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.security.AuthenticatedUserDetails;
import fr.lirmm.aren.service.DebateService;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSink;

/**
 * JAX-RS resource class for Server Sent Event
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
@Path("events")
@Produces(MediaType.SERVER_SENT_EVENTS)
public class BroadcasterRESTFacade {

    @Inject
    private DebateService debateService;

    @Inject
    private BroadcasterService broadcasterService;

    /**
     *
     */
    @Context
    protected SecurityContext securityContext;

    /**
     *
     * @return
     */
    protected User getUser() {
        return ((AuthenticatedUserDetails) securityContext.getUserPrincipal()).getUser();
    }

    /**
     * Listen to all the new created Comments
     *
     * @param sseEventSink
     */
    @GET
    @Path("comments")
    @RolesAllowed({"ADMIN"})
    public void openDebatesListener(@Context SseEventSink sseEventSink) {

        broadcasterService.openListener(-1L, Debate.class, sseEventSink);
    }

    /**
     * Listen to the new created Comments on a specific Debate
     *
     * @param debateId of the Debate to be listen to
     * @param sseEventSink
     */
    @GET
    @Path("comments/{debateId}")
    @RolesAllowed({"GUEST"})
    public void openDebateListener(@PathParam("debateId") Long debateId, @Context SseEventSink sseEventSink) {

        debateService.findByUser(debateId, getUser(), false, false, false, false, false);
        broadcasterService.openListener(debateId, Debate.class, sseEventSink);
    }

    /**
     * Listen to the new created Notifications of the current User
     *
     * @param sseEventSink
     */
    @GET
    @Path("notifications")
    @RolesAllowed({"USER"})
    public void openNotificationListener(@Context SseEventSink sseEventSink) {

        broadcasterService.openListener(getUser().getId(), Notification.class, sseEventSink);
    }
}
