package fr.lirmm.aren.ws.exceptionmapper;

import fr.lirmm.aren.exception.AbstractException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import fr.lirmm.aren.exception.AccessDeniedException;
import fr.lirmm.aren.model.ws.ApiErrorDetails;

/**
 * Exception mapper for {@link AccessDeniedException}s.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Provider
abstract class AbstractExceptionMapper<T extends Exception> implements ExceptionMapper<T> {

    @Context
    private UriInfo uriInfo;

    protected abstract Status getStatus();

    protected abstract String getTitle();

    /**
     *
     * @param exception
     * @return
     */
    @Override
    public Response toResponse(T exception) {

        ApiErrorDetails errorDetails = new ApiErrorDetails();

        errorDetails.setPath(uriInfo.getAbsolutePath().getPath());

        errorDetails.setStatus(this.getStatus().getStatusCode());
        errorDetails.setTitle(this.getTitle());

        errorDetails.setMessage(exception.getMessage());

        if (exception instanceof AbstractException) {
            errorDetails.setDetails(((AbstractException) exception).getDetails());
        }

        return Response.status(this.getStatus().getStatusCode()).entity(errorDetails).type(MediaType.APPLICATION_JSON).build();
    }
}
