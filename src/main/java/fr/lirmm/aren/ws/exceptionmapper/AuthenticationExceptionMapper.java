package fr.lirmm.aren.ws.exceptionmapper;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import fr.lirmm.aren.exception.AuthenticationException;

/**
 * Exception mapper for {@link AuthenticationException}s.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Provider
public class AuthenticationExceptionMapper extends AbstractExceptionMapper<AuthenticationException> {

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
        return "Authentication error";
    }
}
