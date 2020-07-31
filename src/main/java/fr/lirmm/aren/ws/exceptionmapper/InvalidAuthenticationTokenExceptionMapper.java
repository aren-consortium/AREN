package fr.lirmm.aren.ws.exceptionmapper;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import fr.lirmm.aren.exception.InvalidAuthenticationTokenException;

/**
 * Exception mapper for {@link InvalidAuthenticationTokenExceptionMapper}s.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Provider
public class InvalidAuthenticationTokenExceptionMapper extends AbstractExceptionMapper<InvalidAuthenticationTokenException> {

    /**
     *
     * @return
     */
    @Override
    protected Status getStatus() {
        return Status.UNAUTHORIZED;
    }

    /**
     *
     * @return
     */
    @Override
    protected String getTitle() {
        return "Invalid token";
    }
}
