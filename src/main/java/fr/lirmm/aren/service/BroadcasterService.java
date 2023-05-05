package fr.lirmm.aren.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import fr.lirmm.aren.model.AbstractEntity;
import fr.lirmm.aren.model.Comment;
import fr.lirmm.aren.model.Debate;
import fr.lirmm.aren.model.Notification;

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

    /**
     * Store broadcasters (connected clients) by entity Id, by entity Type (Debate, Notification, ...)
     * So each client listens to specific entity change
     */ 
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

    public void broadcast(AbstractEntity object) {
        Long id = object.getId();
        Class<? extends AbstractEntity> which = object.getClass();

        if (object instanceof Notification) {
          id = ((Notification)object).getOwner().getId();
        } else if (object instanceof Comment) {
          id = ((Comment)object).getDebate().getId();
          which = Debate.class;
        }

        if (broadcasters.containsKey(which)) {

            OutboundSseEvent sseEvent = this.eventBuilder
                    .name(object.getClass().getSimpleName())
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .data(object)
                    .reconnectDelay(5000)
                    .build();

            if (broadcasters.get(which).containsKey(id)) {
                broadcasters.get(which).get(id).broadcast(sseEvent);
            }
            // Special key to listen for ALL entities
            if (broadcasters.get(which).containsKey(-1L)) {
                broadcasters.get(which).get(-1L).broadcast(sseEvent);
            }
        }
    }

    /**
     *
     * @param entities
     */
    public void broadcast(List<? extends AbstractEntity> entities) {
      entities.forEach(this::broadcast);
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
}
