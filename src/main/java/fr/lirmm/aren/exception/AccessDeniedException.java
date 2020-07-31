package fr.lirmm.aren.exception;

/**
 * Thrown if errors occur during the authorization process.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
public class AccessDeniedException extends AbstractException {
    
    /**
     *
     */
    private static final long serialVersionUID = -6029917646671248044L;

    /**
     *
     * @param klass
     * @param id
     * @return
     */
    public final static AccessDeniedException UNMUTABLE_OBJECT(Class klass, Long id) {
        AccessDeniedException ade = new AccessDeniedException("unmutable_object");
        ade.details.put("type", klass.getSimpleName());
        ade.details.put("id", id+"");
        return ade;
    }
    
    /**
     *
     * @param klass
     * @param id
     * @return
     */
    public final static AccessDeniedException UNERASABLE_OBJECT(Class klass, Long id) {
        AccessDeniedException ade = new AccessDeniedException("unerasable_object");
        ade.details.put("type", klass.getSimpleName());
        ade.details.put("id", id+"");
        return ade;
    }

    /**
     *
     * @return
     */
    public final static AccessDeniedException PERMISSION_MISSING() {
        return new AccessDeniedException("permission_missing");
    }

    /**
     *
     * @param string
     */
    private AccessDeniedException(String string) {
        super(string);
    }
}
