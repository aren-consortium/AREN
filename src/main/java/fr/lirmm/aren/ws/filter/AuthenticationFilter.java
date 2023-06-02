package fr.lirmm.aren.ws.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import fr.lirmm.aren.model.User;
import fr.lirmm.aren.security.AuthenticatedUserDetails;
import fr.lirmm.aren.security.TokenBasedSecurityContext;
import fr.lirmm.aren.security.token.AuthenticationTokenDetails;
import fr.lirmm.aren.security.token.AuthenticationTokenService;
import fr.lirmm.aren.service.UserService;

/**
 * Authentication filter. Populate the Security Context with the right user
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Provider
@Dependent
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private UserService userService;

    @Inject
    private AuthenticationTokenService authenticationTokenService;

    @Context
    private ResourceInfo resourceInfo;

    /**
     *
     * @param requestContext
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        Method method = resourceInfo.getResourceMethod();
        if (!method.isAnnotationPresent(PermitAll.class)) {

          boolean isAuth = false;

            // Token parameter authentification
            List<String> authorizationPathParam = requestContext.getUriInfo().getQueryParameters().get("token");
            if (authorizationPathParam != null) {
                String authenticationToken = authorizationPathParam.get(0);
                isAuth = handleTokenBasedAuthentication(authenticationToken, requestContext);
            }

            // Authorization header authentification
            String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            if (!isAuth && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String authenticationToken = authorizationHeader.substring(7);
                isAuth = handleTokenBasedAuthentication(authenticationToken, requestContext);
            }

            // Authorization cookie authentification
            if (!isAuth) {
                for (Cookie c : requestContext.getCookies().values()) {
                    if (c.getName().equals(HttpHeaders.AUTHORIZATION)) {
                        isAuth = handleTokenBasedAuthentication(c.getValue(), requestContext);
                    }
                }
            }

            // Authentificate as guest
            if (!isAuth) {
              AuthenticatedUserDetails authenticatedUserDetails = new AuthenticatedUserDetails(new User());
              boolean isSecure = requestContext.getSecurityContext().isSecure();
              SecurityContext securityContext = new TokenBasedSecurityContext(authenticatedUserDetails, null, isSecure);
              requestContext.setSecurityContext(securityContext);
            }

            // CAS authentification
            // Other authentication schemes (such as Basic) could be supported
        }
    }

    private boolean handleTokenBasedAuthentication(String authenticationToken, ContainerRequestContext requestContext) {

        AuthenticationTokenDetails authenticationTokenDetails = authenticationTokenService.parseToken(authenticationToken);
        User user = userService.findByUsernameOrEmail(authenticationTokenDetails.getUsername());
        if (user == null) {
            //throw InvalidAuthenticationTokenException.INVALID_TOKEN();
            return false;
        }
        if (user.getTokenValidity().isAfter(authenticationTokenDetails.getIssuedDate())) {
            //throw InvalidAuthenticationTokenException.EXPIRED_TOKEN();
            return false;
        }

        // userService.updateLastLogin(user);
        AuthenticatedUserDetails authenticatedUserDetails = new AuthenticatedUserDetails(user);

        boolean isSecure = requestContext.getSecurityContext().isSecure();
        SecurityContext securityContext = new TokenBasedSecurityContext(authenticatedUserDetails, authenticationTokenDetails, isSecure);
        requestContext.setSecurityContext(securityContext);
        return true;
    }
}
