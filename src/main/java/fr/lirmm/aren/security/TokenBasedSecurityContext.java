package fr.lirmm.aren.security;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import fr.lirmm.aren.model.User.Authority;
import fr.lirmm.aren.security.token.AuthenticationTokenDetails;

/**
 * {@link SecurityContext} implementation for token-based authentication.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
public class TokenBasedSecurityContext implements SecurityContext {

    private final AuthenticatedUserDetails authenticatedUserDetails;
    private final AuthenticationTokenDetails authenticationTokenDetails;
    private final boolean secure;

    /**
     *
     * @param authenticatedUserDetails
     * @param authenticationTokenDetails
     * @param secure
     */
    public TokenBasedSecurityContext(AuthenticatedUserDetails authenticatedUserDetails, AuthenticationTokenDetails authenticationTokenDetails, boolean secure) {
        this.authenticatedUserDetails = authenticatedUserDetails;
        this.authenticationTokenDetails = authenticationTokenDetails;
        this.secure = secure;
    }

    /**
     *
     * @return
     */
    @Override
    public Principal getUserPrincipal() {
        return authenticatedUserDetails;
    }

    /**
     *
     * @param s
     * @return
     */
    @Override
    public boolean isUserInRole(String s) {
        return authenticatedUserDetails.getUser().is(Authority.valueOf(s));
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isSecure() {
        return secure;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }

    /**
     *
     * @return
     */
    public AuthenticationTokenDetails getAuthenticationTokenDetails() {
        return authenticationTokenDetails;
    }
}
