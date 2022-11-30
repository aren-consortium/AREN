package fr.lirmm.aren.ws.rest;

import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import fr.lirmm.aren.model.Institution;
import fr.lirmm.aren.producer.Configurable;
import fr.lirmm.aren.service.InstitutionService;

/**
 * JAX-RS resource class for Institutions managment
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("institutions")
public class InstitutionRESTFacade extends AbstractRESTFacade<Institution> {

    @Inject
    private InstitutionService institutionService;
    
    @Inject
    @Configurable("default.institution.id")
    private Provider<Long> noInstitutionId;

    /**
     *
     * @return
     */
    @Override
    protected InstitutionService getService() {
        return institutionService;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRemovable(Institution institution) {
      return (institution.getId() != noInstitutionId.get());
    }

    /**
     *
     * @param institution
     * @return
     */
    @Override
    @RolesAllowed({"ADMIN"})
    public Institution create(Institution institution) {

        return super.create(institution);
    }

    /**
     *
     * @param id
     * @param institution
     * @return
     */
    @Override
    @RolesAllowed({"ADMIN"})
    public Institution edit(Long id, Institution institution) {

        return super.edit(id, institution);
    }

    /**
     *
     * @param id
     */
    @Override
    @RolesAllowed({"ADMIN"})
    public void remove(Long id) {

        super.remove(id);
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Institution find(Long id) {
        Institution institution = super.find(id);
        institution.getUsers().size();
        institution.getTeams().size();
        return institution;
    }

    /**
     * Find all Institution with associated Teams and associated Users
     *
     * @return
     */
    @GET
    @Path("/deep")
    @RolesAllowed({"MODO"})
    public Set<Institution> findAllDeep() {
        return institutionService.findAll(true, true);
    }

    /**
     * Find all Institution with associated Teams and associated Users
     *
     * @return
     */
    @RolesAllowed({"MODO"})
    @Override
    public Set<Institution> findAll() {
        return super.findAll();
    }

}
