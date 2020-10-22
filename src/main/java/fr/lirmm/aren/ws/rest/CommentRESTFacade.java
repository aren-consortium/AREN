package fr.lirmm.aren.ws.rest;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import fr.lirmm.aren.service.CommentService;
import fr.lirmm.aren.model.Comment;
import fr.lirmm.aren.model.TagSet;
import fr.lirmm.aren.service.BroadcasterService;
import fr.lirmm.aren.service.HttpRequestService;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

/**
 * JAX-RS resource class for Comments managment
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("comments")
public class CommentRESTFacade extends AbstractRESTFacade<Comment> {

    @Inject
    private CommentService commentService;

    @Inject
    private BroadcasterService broadcasterService;

    @Inject
    private HttpRequestService httpRequestService;

    /**
     *
     * @return
     */
    @Override
    protected CommentService getService() {
        return commentService;
    }

    /**
     *
     * @param id
     * @param comment
     * @return
     */
    @Override
    @RolesAllowed({"USER"})
    public Comment edit(Long id, Comment comment) {
        Comment toEdit = find(id);
        safetyPreEdition(comment, toEdit);

        boolean sameProposedTags = comment.getProposedTags().equals(toEdit.getProposedTags());
        boolean sameText = comment.getReformulation().equals(toEdit.getReformulation());

        if (!getUser().is("SUPERADMIN") && !sameProposedTags) {
            toEdit.setProposedTags(comment.getProposedTags());
        } else if (getUser().is("SUPERADMIN")) {
            toEdit.merge(comment);
        }

        if (!sameProposedTags || !sameText) {
            // Reinit tags
            toEdit.setTags(new TagSet());
        }
        commentService.edit(toEdit);

        if (!sameProposedTags || !sameText) {
            broadcasterService.broadcastComment(toEdit);
            commentService.updateTags(toEdit);
        }
        broadcasterService.broadcastComment(toEdit);
        return toEdit;
    }

    /**
     * Get the scalar corespondance weight between the Comment's selection and
     * the Comment's argumentation Use a remote service
     *
     * @param comment to proceed
     * @return
     */
    @POST
    @Path("scalar")
    @RolesAllowed({"GUEST"})
    public String getScalar(Comment comment) {
        return httpRequestService.getScalar(comment);
    }

    /**
     * Update tags of all comments Use a remote service
     *
     */
    @PUT
    @Path("updateTags")
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void updateAllTags(@Context SseEventSink eventSink, @Context Sse sse) {
        commentService.updateAllTags((Comment comment, Float progress) -> {
            eventSink.send(sse.newEvent(progress + ""));
            broadcasterService.broadcastComment(comment);
        });
        eventSink.send(sse.newEvent("1"));
        eventSink.close();
    }

    /**
     * Update Tags of a single Comment Use a remote servie
     *
     * @param id of the Comment to update the Tags
     * @return
     */
    @PUT
    @Path("{id}/updateTags")
    @RolesAllowed({"ADMIN"})
    public TagSet updateTag(@PathParam("id") Long id) {
        Comment comment = commentService.find(id);
        commentService.updateTags(comment);
        broadcasterService.broadcastComment(comment);
        return comment.getTags();
    }
}
