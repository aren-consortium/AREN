package fr.lirmm.aren.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import fr.lirmm.aren.model.AbstractEntity;
import fr.lirmm.aren.model.Comment;
import fr.lirmm.aren.model.Debate;
import fr.lirmm.aren.model.Notification;
import fr.lirmm.aren.security.AuthenticatedUserDetails;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

/**
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class BroadcasterService {

    @Context
    private Sse sse;

    /**
     *
     */
    @Context
    protected SecurityContext securityContext;

    private OutboundSseEvent.Builder eventBuilder;

    private final Map<Class<? extends AbstractEntity>, HashMap<Long, SseBroadcaster>> broadcasters = new HashMap<Class<? extends AbstractEntity>, HashMap<Long, SseBroadcaster>>();

    /**
     *
     * @param sse
     */
    @Context
    public void setSse(Sse sse) {
        this.sse = sse;
        this.eventBuilder = sse.newEventBuilder();
    }

    private void broadcast(Long id, AbstractEntity object, Class<? extends AbstractEntity> which) {

        if (broadcasters.containsKey(which)) {

            OutboundSseEvent sseEvent = this.eventBuilder
                    .comment(((AuthenticatedUserDetails) securityContext.getUserPrincipal()).getUser().getId() + "")
                    .name("message")
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .data(which, object)
                    .reconnectDelay(5000)
                    .build();

            if (broadcasters.get(which).containsKey(id)) {
                broadcasters.get(which).get(id).broadcast(sseEvent);
            }
            if (broadcasters.get(which).containsKey(-1L)) {
                broadcasters.get(which).get(-1L).broadcast(sseEvent);
            }
        }
    }

    /**
     *
     * @param id
     * @param which
     * @param sink
     */
    public void openListener(Long id, Class<? extends AbstractEntity> which, SseEventSink sink) {

        if (!broadcasters.containsKey(which)) {
            broadcasters.put(which, new HashMap<Long, SseBroadcaster>());
        }

        if (!broadcasters.get(which).containsKey(id)) {
            broadcasters.get(which).put(id, sse.newBroadcaster());
        }
        broadcasters.get(which).get(id).register(sink);
        sink.send(sse.newEvent("registered", "true"));
    }

    /**
     *
     * @param comment
     */
    public void broadcastComment(Comment comment) {
        broadcast(comment.getDebate().getId(), comment, Debate.class);
    }

    /**
     *
     * @param notif
     */
    public void broadcastNotification(Notification notif) {
        broadcast(notif.getOwner().getId(), notif, Notification.class);
    }

    /**
     *
     * @param notifs
     */
    public void broadcastNotification(List<Notification> notifs) {
        notifs.forEach(notif -> {
            broadcastNotification(notif);
        });
    }
}
