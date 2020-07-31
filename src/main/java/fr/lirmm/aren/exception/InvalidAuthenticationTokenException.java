package fr.lirmm.aren.exception;

/**
 * Thrown if an authentication token is invalid.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
public class InvalidAuthenticationTokenException extends AbstractException {

    /**
     *
     */
    private static final long serialVersionUID = -2394710689872197849L;

    /**
     *
     * @return
     */
    public final static InvalidAuthenticationTokenException INVALID_CAS_TICKET() {
        return new InvalidAuthenticationTokenException("invalid_cas_ticket");
    }

    /**
     *
     * @param code
     * @return
     */
    public final static InvalidAuthenticationTokenException INVALID_CAS_TICKET(String code) {
        InvalidAuthenticationTokenException iate = InvalidAuthenticationTokenException.INVALID_CAS_TICKET();
        iate.details.put("code", code);
        return iate;
    }

    /**
     *
     * @return
     */
    public final static InvalidAuthenticationTokenException INVALID_TOKEN() {
        return new InvalidAuthenticationTokenException("invalid_token");
    }

    /**
     *
     * @return
     */
    public final static InvalidAuthenticationTokenException EXPIRED_TOKEN() {
        return new InvalidAuthenticationTokenException("expired_token");
    }

    /**
     *
     * @param claimName
     * @return
     */
    public final static InvalidAuthenticationTokenException INVALID_CLAIM(String claimName) {
        InvalidAuthenticationTokenException iate = new InvalidAuthenticationTokenException("invalid_claim");
        iate.details.put("claimName", claimName);
        return iate;
    }

    /**
     *
     * @param string
     */
    private InvalidAuthenticationTokenException(String string) {
        super(string);
    }
}
