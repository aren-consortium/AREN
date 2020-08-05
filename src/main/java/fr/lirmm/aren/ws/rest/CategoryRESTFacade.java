package fr.lirmm.aren.ws.rest;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import fr.lirmm.aren.service.CategoryService;
import fr.lirmm.aren.model.Category;
import java.util.HashSet;

/**
 * JAX-RS resource class for Categories managment
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("categories")
public class CategoryRESTFacade extends AbstractRESTFacade<Category> {

    @Inject
    private CategoryService categoryService;

    /**
     *
     * @return
     */
    @Override
    protected CategoryService getService() {
        return categoryService;
    }

    /**
     *
     * @param category
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Category create(Category category) {
        return super.create(category);
    }

    /**
     *
     * @param category
     * @return
     */
    @Override
    @RolesAllowed({"MODO"})
    public Category edit(Long id, Category category) {
        return super.edit(id, category);
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"GUEST"})
    public HashSet<Category> findAll() {
        boolean withDocument = false;
        if (getUser().is("MODO")) {
            withDocument = this.overview == null;
        }
        return categoryService.findAllByUser(getUser(), withDocument);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    @RolesAllowed({"GUEST"})
    public Category find(Long id) {
        boolean withDocument = false;
        if (getUser().is("MODO")) {
            withDocument = this.overview == null;
        }
        return categoryService.findByUser(id, getUser(), withDocument);
    }
}
