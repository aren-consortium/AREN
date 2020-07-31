package fr.lirmm.aren.exception;

/**
 * Thrown if errors occur during the authentication process.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
public class AuthenticationException extends AbstractException {

    /**
     *
     */
    private static final long serialVersionUID = -315540945651188131L;

    /**
     *
     * @return
     */
    public final static AuthenticationException BAD_CREDENTIALS() {
        return new AuthenticationException("bad_credentials");
    }

    /**
     *
     * @param username
     * @return
     */
    public final static AuthenticationException INACTIVE_USER(String username) {
        AuthenticationException ae = new AuthenticationException("inactive_user:" + username);
        ae.details.put("username", username);
        return ae;
    }

    /**
     *
     * @return
     */
    public final static AuthenticationException AUTHENTIFICATION_REQUIRED() {
        return new AuthenticationException("authentification_required");
    }

    /**
     *
     * @param string
     */
    private AuthenticationException(String string) {
        super(string);
    }
}
