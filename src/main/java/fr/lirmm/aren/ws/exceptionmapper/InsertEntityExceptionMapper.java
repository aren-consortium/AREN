package fr.lirmm.aren.ws.exceptionmapper;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import fr.lirmm.aren.exception.InsertEntityException;

/**
 * Exception mapper for {@link InsertEntityExceptionMapper}s.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Provider
public class InsertEntityExceptionMapper extends AbstractExceptionMapper<InsertEntityException> {
    
    /**
     *
     * @return
     */
    @Override
    protected Status getStatus() {
        return Status.BAD_REQUEST;
    }

    /**
     *
     * @return
     */
    @Override
    protected String getTitle() {
        return "Insertion error";
    }
}
