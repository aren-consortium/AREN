package fr.lirmm.aren.ws.exceptionmapper;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import fr.lirmm.aren.exception.AccessDeniedException;

/**
 * Exception mapper for {@link AccessDeniedException}s.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Provider
public class AccessDeniedExceptionMapper extends AbstractExceptionMapper<AccessDeniedException> {

    /**
     *
     * @return
     */
    @Override
    protected Status getStatus() {
        return Status.FORBIDDEN;
    }

    /**
     *
     * @return
     */
    @Override
    protected String getTitle() {
        return "Access denied";
    }
}
