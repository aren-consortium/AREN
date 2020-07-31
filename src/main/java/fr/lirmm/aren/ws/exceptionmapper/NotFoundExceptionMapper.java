package fr.lirmm.aren.ws.exceptionmapper;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import fr.lirmm.aren.exception.AccessDeniedException;
import javax.ws.rs.NotFoundException;

/**
 * Exception mapper for {@link AccessDeniedException}s.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Provider
public class NotFoundExceptionMapper extends AbstractExceptionMapper<NotFoundException> {

    /**
     *
     * @return
     */
    @Override
    protected Status getStatus() {
        return Status.NOT_FOUND;
    }

    /**
     *
     * @return
     */
    @Override
    protected String getTitle() {
        return "Resource not found";
    }
}
