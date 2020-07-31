package fr.lirmm.aren.ws.rest;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import fr.lirmm.aren.service.InstitutionService;
import fr.lirmm.aren.service.TeamService;
import fr.lirmm.aren.service.UserService;
import fr.lirmm.aren.model.Team;
import java.util.Set;

/**
 * JAX-RS resource class for Teams managment
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("teams")
public class TeamRESTFacade extends AbstractRESTFacade<Team> {

    @Inject
    private TeamService teamService;

    @Inject
    private UserService userService;

    @Inject
    private InstitutionService institutionService;

    /**
     *
     */
    @QueryParam("standalone")
    protected String standalone;

    /**
     *
     * @return
     */
    @Override
    protected TeamService getService() {
        return teamService;
    }

    /**
     *
     * @param team
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Team create(Team team) {

        if (team.getInstitution() == null) {
            team.setInstitution(institutionService.getReference(0L));
        }

        return super.create(team);
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
     *
     * @param team
     * @param id
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Team edit(Long id, Team team) {

        return super.edit(id, team);
    }

    /**
     *
     * @return
     */
    @RolesAllowed({"MODO"})
    @Override
    public Team find(Long id) {
        Team team = super.find(id);
        team.getUsers().size();
        return team;
    }

    /**
     *
     * @return
     */
    @RolesAllowed({"MODO"})
    @Override
    public Set<Team> findAll() {

        boolean standalone = this.standalone != null;
        return teamService.findAll(standalone);
    }

    /**
     * Add a User to a Team
     *
     * @param id of the Team
     * @param userId of the User
     */
    @PUT
    @Path("{id}/users/{userId}")
    @RolesAllowed({"MODO"})
    public void addUser(@PathParam("id") Long id, @PathParam("userId") Long userId) {

        Team team = this.find(id);
        team.addUser(userService.getReference(userId));
        teamService.edit(team);
    }

    /**
     * Remove a user from a Team
     *
     * @param id of the Team
     * @param userId of teh User
     */
    @DELETE
    @Path("{id}/users/{userId}")
    @RolesAllowed({"MODO"})
    public void removeUser(@PathParam("id") Long id, @PathParam("userId") Long userId) {

        Team team = this.find(id);
        team.removeUser(userService.getReference(userId));
        teamService.edit(team);
    }
}
