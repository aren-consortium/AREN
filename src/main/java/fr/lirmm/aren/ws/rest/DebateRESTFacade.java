package fr.lirmm.aren.ws.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import fr.lirmm.aren.service.BroadcasterService;
import fr.lirmm.aren.service.CommentService;
import fr.lirmm.aren.service.DebateService;
import fr.lirmm.aren.service.NotificationService;
import fr.lirmm.aren.service.TeamService;
import fr.lirmm.aren.service.UserService;
import fr.lirmm.aren.exception.InsertEntityException;
import fr.lirmm.aren.model.Comment;
import fr.lirmm.aren.model.Debate;
import fr.lirmm.aren.model.Notification;
import fr.lirmm.aren.model.Team;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.ws.Scrap;
import fr.lirmm.aren.service.HttpRequestService;
import fr.lirmm.aren.service.ODFService;
import java.io.File;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * JAX-RS resource class for Debates managment
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("debates")
public class DebateRESTFacade extends AbstractRESTFacade<Debate> {

    @Inject
    private DebateService debateService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private CommentService commentService;

    @Inject
    private CommentRESTFacade commentFacade;

    @Inject
    private BroadcasterService broadcasterService;

    @Inject
    private TeamService teamService;

    @Inject
    private UserService userService;

    @Inject
    private ODFService odfService;

    @Inject
    private HttpRequestService httpRequestService;

    /**
     *
     */
    @QueryParam("category")
    protected Long category;

    /**
     *
     * @return
     */
    @Override
    protected DebateService getService() {
        return debateService;
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"GUEST"})
    public Set<Debate> findAll() {
        boolean withComments = (this.overview == null);
        boolean isModo = getUser().is("MODO");
        return debateService.findAllByUser(getUser(), category, true, withComments, isModo, isModo, false);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    @RolesAllowed({"GUEST"})
    public Debate find(Long id) {

        boolean withComments = (this.overview == null);
        return debateService.findByUser(id, getUser(), true, withComments, true, true, false);
    }

    /**
     *
     * @param debate
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Debate create(Debate debate) {
        Set<Team> teams = debate.getTeams();
        Set<User> guests = debate.getGuests();
        super.create(debate);
        debate.getTeams().addAll(teams);
        debate.getGuests().addAll(guests);
        debateService.edit(debate);

        Debate newDebate = debateService.find(debate.getId(), false, false, true, true, true);

        List<Notification> notifications = new ArrayList<Notification>();

        newDebate.getTeams().forEach((Team team) -> {
            team.getUsers().forEach((User user) -> {
                notifications.add(Notification.TEAM_ADDED_TO_DEBATE(user, newDebate, team));
            });
        });

        newDebate.getGuests().forEach((User user) -> {
            notifications.add(Notification.INVITED_TO_DEBATE(user, newDebate));
        });

        notificationService.create(notifications);
        broadcasterService.broadcastNotification(notifications);

        return newDebate;
    }

    /**
     *
     * @param id
     */
    @Override
    @RolesAllowed({"MODO"})
    public void remove(Long id) {
        super.remove(id);
    }

    /**
     * Creates a new Comment onto a Debate
     *
     * @param id of the debate
     * @param comment to add
     * @return
     */
    @POST
    @Path("{id}/comments")
    @RolesAllowed({"USER"})
    public Comment addComment(@PathParam("id") Long id, Comment comment) {

        Debate debate = debateService.findByUser(id, getUser(), false, false, false, false, false);
        Comment parent = null;

        if (comment.getParent() != null) {
            parent = commentService.getReference(comment.getParent().getId());
            if (!Objects.equals(debate, parent.getDebate())) {
                throw InsertEntityException.INVALID_PARENT();
            }
        }

        commentFacade.create(comment);

        List<Notification> notifications = new ArrayList<Notification>();
        List<User> users = new ArrayList<User>();
        while (parent != null) {
            // Only one notification per user
            // And not notify oneself
            if (!users.contains(parent.getOwner()) && parent.getOwner() != getUser()) {
                notifications.add(Notification.COMMENT_ANSWERED(parent.getOwner(), comment));
                users.add(parent.getOwner());
            }
            parent = parent.getParent();
        }

        notificationService.create(notifications);
        broadcasterService.broadcastNotification(notifications);
        broadcasterService.broadcastComment(comment);

        commentService.updateTags(comment); // It is long
        broadcasterService.broadcastComment(comment);

        return comment;
    }

    /**
     * Remove all the comment of a debate
     *
     * @param id
     */
    @DELETE
    @Path("{id}/comments")
    @RolesAllowed({"ADMIN"})
    public void clear(@PathParam("id") Long id) {

        debateService.clearComments(id);
    }

    /**
     * Mark a Comment in a Debate as being signaled
     *
     * @param id of the debate holding the comment
     * @param commentId of the comment to be signaled
     * @return
     */
    @PUT
    @Path("{id}/signal/{commentId}")
    @RolesAllowed({"USER"})
    public Comment signal(@PathParam("id") Long id, @PathParam("commentId") Long commentId) {

        Debate debate = debateService.findByUser(id, getUser(), false, false, false, false, false);
        Comment comment = commentService.find(commentId);

        if (comment.getDebate().equals(debate)) {

            comment.setSignaled(!comment.isSignaled());
            commentService.edit(comment);

            Set<User> guests = debate.getGuests();
            guests.size();

            List<Notification> notifications = new ArrayList<Notification>();

            guests.forEach((User user) -> {
                if (user.is("MODO")) {
                    notifications.add(Notification.COMMENT_SINGNALED(user, comment));
                }
            });

            notificationService.create(notifications);
            broadcasterService.broadcastNotification(notifications);
        }
        return comment;
    }

    /**
     * Mark a Comment in a Debate as being moderated
     *
     * @param id of the debate holding the comment
     * @param commentId of the comment to be moderated
     * @return
     */
    @PUT
    @Path("{id}/moderate/{commentId}")
    @RolesAllowed({"MODO"})
    public Comment moderate(@PathParam("id") Long id, @PathParam("commentId") Long commentId) {

        debateService.findByUser(id, getUser(), false, false, false, false, false);

        Comment comment = commentService.find(id);
        comment.setModerated(!comment.isModerated());
        commentService.edit(comment);

        Notification notif = Notification.COMMENT_MODERATED(comment);
        notificationService.create(notif);
        broadcasterService.broadcastNotification(notif);

        return comment;
    }

    /**
     * Add a Team to participate in a Debate
     *
     * @param id of the debate
     * @param teamId of the team
     */
    @PUT
    @Path("{id}/teams/{teamId}")
    @RolesAllowed({"MODO"})
    public void addTeam(@PathParam("id") Long id, @PathParam("teamId") Long teamId) {

        Debate debate = debateService.findByUser(id, getUser(), false, false, true, false, false);

        Team team = teamService.find(teamId);
        debate.addTeam(team);
        debateService.edit(debate);

        List<Notification> notifications = new ArrayList<Notification>();

        team.getUsers().forEach((User user) -> {
            notifications.add(Notification.TEAM_ADDED_TO_DEBATE(user, debate, team));
        });

        notificationService.create(notifications);
        broadcasterService.broadcastNotification(notifications);

    }

    /**
     * Remove a Team to participate in a Debate
     *
     * @param id of the debate
     * @param teamId of the team
     */
    @DELETE
    @Path("{id}/teams/{teamId}")
    @RolesAllowed({"MODO"})
    public void removeTeam(@PathParam("id") Long id, @PathParam("teamId") Long teamId) {

        Debate debate = debateService.findByUser(id, getUser(), false, false, true, false, false);

        Team team = teamService.find(teamId);
        debate.removeTeam(team);
        debateService.edit(debate);

    }

    /**
     * Add a User to participate in a Debate
     *
     * @param id of the debate
     * @param userId of the user
     */
    @PUT
    @Path("{id}/users/{userId}")
    @RolesAllowed({"ADMIN", "MODO"})
    public void addGuest(@PathParam("id") Long id, @PathParam("userId") Long userId) {

        Debate debate = debateService.findByUser(id, getUser(), false, false, false, true, false);

        User user = userService.find(userId);
        debate.addGuest(user);
        debateService.edit(debate);

        Notification notif = Notification.INVITED_TO_DEBATE(user, debate);
        notificationService.create(notif);
        broadcasterService.broadcastNotification(notif);
    }

    /**
     * Remove a User to participate in a Debate
     *
     * @param id of the debate
     * @param userId of the user
     */
    @DELETE
    @Path("{id}/users/{userId}")
    @RolesAllowed({"MODO"})
    public void removeGuest(@PathParam("id") Long id, @PathParam("userId") Long userId) {

        Debate debate = debateService.findByUser(id, getUser(), false, false, false, true, false);

        User user = userService.find(userId);
        debate.removeGuest(user);
        debateService.edit(debate);
    }

    /**
     * Calculate the intersections of the selections of the Comment of a Debate
     *
     * @param id of the Debate
     * @return a list of Strings
     */
    @GET
    @Path("{id}/scraps")
    @RolesAllowed({"GUEST"})
    public List<Scrap> getScraps(@PathParam("id") Long id) {
        Debate debate = this.find(id);
        ArrayList<Scrap> scraps = new ArrayList<Scrap>();

        for (Comment comment : debate.getComments()) {
            if (comment.getParent() == null) {
                Scrap scrap = new Scrap();
                scrap.setStartContainer(comment.getStartContainer());
                scrap.setStartOffset(comment.getStartOffset());
                scrap.setEndContainer(comment.getEndContainer());
                scrap.setEndOffset(comment.getEndOffset());
                scrap.getComments().add(comment);
                scraps.add(scrap);
            }
        }

        int firstsLen = scraps.size();
        int checkingStart = 0;
        int checkingEnd = firstsLen;
        // While there are new scraps, test to find new overlaps
        while (checkingStart != checkingEnd) {
            // For all initial scraps
            for (int i = 0; i < firstsLen; i++) {
                Scrap s1 = scraps.get(i);
                // For all new scraps
                for (int j = (checkingStart == 0 ? i + 1 : checkingStart); j < checkingEnd; j++) {
                    Scrap s2 = scraps.get(j);
                    int comparison = s1.compareTo(s2);
                    if (comparison != Scrap.ALL_BEFORE && comparison != Scrap.ALL_AFTER) {
                        Scrap start = s1;
                        Scrap end = s1;
                        // The higest start is the start of the ovelap
                        if (comparison == Scrap.OVER_BEFORE || comparison == Scrap.COVERS) {
                            start = s2;
                        }
                        // The smalest end is the end of the ovelap
                        if (comparison == Scrap.OVER_AFTER || comparison == Scrap.COVERS) {
                            end = s2;
                        }
                        // Create the scrap
                        Scrap scrap = new Scrap();
                        scrap.setStartContainer(start.getStartContainer());
                        scrap.setStartOffset(start.getStartOffset());
                        scrap.setEndContainer(end.getEndContainer());
                        scrap.setEndOffset(end.getEndOffset());

                        // Check if the scrap already exists
                        int k = 0;
                        while (k < scraps.size() && !scrap.equals(scraps.get(k))) {
                            k++;
                        }

                        if (k < scraps.size()) {
                            scrap = scraps.get(k);
                        } else {
                            scraps.add(scrap);
                        }
                        // Add comments to the new scrap
                        scrap.getComments().addAll(s1.getComments());
                        scrap.getComments().addAll(s2.getComments());
                    }
                }
            }
            // Only check scraps inserted in the last loop
            checkingStart = checkingEnd;
            checkingEnd = scraps.size();
        }

        scraps.sort((Scrap c1, Scrap c2) -> c2.getComments().size() - c1.getComments().size());

        return scraps;
    }

    /**
     * Find the Comments of a Debate that match a theme
     *
     * @param id of the debate
     * @param theme
     * @return
     */
    @GET
    @Path("{id}/theme")
    @RolesAllowed({"GUEST"})
    public String getTheme(@PathParam("id") Long id, @QueryParam("theme") String theme) {
        this.find(id);
        return httpRequestService.getTheme(id, theme);
    }

    /**
     * Update the Tags of all the Comments of a Debate Use a remote service
     *
     * @param id
     */
    @PUT
    @Path("{id}/updateTags")
    @RolesAllowed({"SUPERADMIN"})
    public void updateTags(@PathParam("id") Long id) {
        Debate debate = debateService.findByUser(id, getUser(), false, true, false, false, false);

        debate.getComments().forEach((Comment comment) -> {
            commentService.updateTags(comment, true);
            broadcasterService.broadcastComment(comment);
        });
    }

    /**
     * Export a Debate to an ODF file
     *
     * @param id of the debate
     * @return an ODF file
     * @throws Exception
     */
    @GET
    @Path("{id}/export")
    @RolesAllowed({"GUEST"})
    @Produces("application/odt")
    public Response export(@PathParam("id") Long id) throws Exception {
        Debate debate = debateService.findByUser(id, getUser(), true, true, false, false, false);
        File export = odfService.parseDebate(debate);
        String fileName = debate.getDocument().getName().replaceAll("[^a-zA-Z0-9\\s]", "") + ".odt";
        return Response.ok(export)
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .header("Content-length", export.length())
                .build();
    }
}
