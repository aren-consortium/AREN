package fr.lirmm.aren.ws.rest;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import fr.lirmm.aren.service.DocumentService;
import fr.lirmm.aren.model.Document;
import java.util.Set;

/**
 * JAX-RS resource class for Documents managment
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("documents")
public class DocumentRESTFacade extends AbstractRESTFacade<Document> {

    @Inject
    private DocumentService documentService;

    /**
     *
     * @return
     */
    @Override
    protected DocumentService getService() {
        return documentService;
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Set<Document> findAll() {
        boolean withDebates = this.overview == null;
        return documentService.findAll(withDebates);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Document find(Long id) {
        boolean withDebates = this.overview == null;
        return documentService.find(id, withDebates);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Document create(Document doc) {
        return super.create(doc);
    }

    /**
     * Ducplicate a Documents withe the the associaitons
     *
     * @param id of the Document to duplicate
     * @return the duplicated Document
     */
    @POST
    @Path("{id}/duplicate")
    @RolesAllowed({"MODO"})
    public Document duplicate(@PathParam("id") Long id) {

        Document document = find(id);

        return this.create(document);
    }
}
