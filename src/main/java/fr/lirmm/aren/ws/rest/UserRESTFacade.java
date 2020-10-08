package fr.lirmm.aren.ws.rest;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import fr.lirmm.aren.service.InstitutionService;
import fr.lirmm.aren.service.UserService;
import fr.lirmm.aren.exception.AccessDeniedException;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.ws.ChangePassword;
import fr.lirmm.aren.model.ws.UserCredentials;
import fr.lirmm.aren.producer.Configurable;
import fr.lirmm.aren.security.token.AuthenticationTokenDetails;
import fr.lirmm.aren.security.token.AuthenticationTokenService;
import fr.lirmm.aren.service.AuthentificationService;
import fr.lirmm.aren.service.MailingService;
import fr.lirmm.aren.service.NotificationService;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.security.PermitAll;
import javax.mail.MessagingException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

/**
 * JAX-RS resource class for Users managment
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@RequestScoped
@Path("users")
public class UserRESTFacade extends AbstractRESTFacade<User> {

    @Inject
    private UserService userService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private MailingService mailingService;

    @Inject
    private AuthentificationService authentificationService;

    @Inject
    private AuthenticationTokenService authenticationTokenService;

    @Inject
    private NotificationService notificationService;

    @Inject
    @Configurable("reverse-proxy")
    private String reverseProxy;

    /**
     *
     */
    @QueryParam("standalone")
    protected String standalone;

    /**
     *
     */
    @QueryParam("returnUrl")
    protected String returnUrl;

    /**
     *
     * @return
     */
    @Override
    protected UserService getService() {
        return userService;
    }

    /**
     * Create a token for the User matching the credentials
     *
     * @param credentials to fetch an User
     * @return the token
     */
    @POST
    @PermitAll
    @Path("auth")
    public String authenticate(UserCredentials credentials) {
        User user = authentificationService.validateCredentials(credentials.getUsername(), credentials.getPassword());
        return authenticationTokenService.issueToken(user);
    }

    /**
     * Create a token for the User matching the credentials and encapsulate it
     * in a Cookie
     *
     * @param credentials to fetch an User
     * @return a Cookie
     */
    @POST
    @PermitAll
    @Path("login")
    public Response login(UserCredentials credentials) {
        if (credentials == null) {
            throw new BadRequestException();
        }

        String token = authenticate(credentials);

        int maxAge = -1;
        if (credentials.isRememberMe()) {
            maxAge = 360 * 24 * 60 * 60;
        }

        String domain;
        Response response;
        try {
            domain = new URL(request.getRequestURL().toString()).getHost();
            NewCookie cookie = new NewCookie(HttpHeaders.AUTHORIZATION, token, "/", domain, "Authentification token for Aren platform", maxAge, false, true);
            response = Response.ok(token).cookie(cookie).build();
        } catch (MalformedURLException ex) {
            response = Response.ok(token).build();
        }
        return response;
    }

    /**
     * Remove the Cookie that stored the previously created token
     *
     * @return
     */
    @POST
    @Path("logout")
    @PermitAll
    public Response logout() {
        String domain;
        Response response;
        try {
            domain = new URL(request.getRequestURL().toString()).getHost();
            NewCookie cookie = new NewCookie(HttpHeaders.AUTHORIZATION, "", "/", domain, "", 0, false, true);
            response = Response.ok().cookie(cookie).build();
        } catch (MalformedURLException ex) {
            response = Response.ok().build();
        }
        return response;
    }

    /**
     * 
     * @param id
     * @param entity
     * @return 
     */
    @Override
    @RolesAllowed({"USER"})
    public User edit(Long id, User entity) {
        if (!(getUser().is("MODO") && getUser().hasSameOrMoreRightThan(entity)
                || getUser().getId().equals(entity.getId()))) {
            throw AccessDeniedException.PERMISSION_MISSING();
        }

        if (!getUser().is("SUPERADMIN")) {
            User newUser = new User();
            // The only alterable fields in User
            if (getUser().is("MODO") || getUser().getId().equals(entity.getId())) {
                newUser.setFirstName(entity.getFirstName());
                newUser.setLastName(entity.getLastName());
                newUser.setEmail(entity.getEmail());
            }
            if (getUser().is("MODO") && getUser().hasSameOrMoreRightThan(entity)) {
                newUser.setAuthority(entity.getAuthority());
            }
            entity = newUser;
        }
        return super.edit(id, entity);
    }

    /**
     *
     * @param user
     * @return
     */
    @Override
    @RolesAllowed({"GUEST"})
    public User create(User user) {

        if (user.getInstitution() == null || !getUser().is(User.Authority.SUPERADMIN)) {
            user.setInstitution(institutionService.getReference(0L));
        }
        if (getUser().getAuthority() == User.Authority.GUEST) {
            user.setActive(false);
            super.create(user);
            try {
                sendLink(user, "mail_new_user_subject", "mail_new_user_body");
            } catch (MessagingException ex) {
                super.remove(user.getId());
                throw new RuntimeException();
            }
        } else if (getUser().hasSameOrMoreRightThan(user)) {
            super.create(user);
        } else {
            throw AccessDeniedException.PERMISSION_MISSING();
        }
        return user;
    }

    /**
     *
     * @param user
     * @param subject
     * @param body
     * @param defautReturnUrl
     */
    private void sendLink(User user, String subject, String body) throws MessagingException {
        Locale currentLocale = request.getLocale();
        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale);

        String token = authenticationTokenService.issueToken(user, 24L * 60 * 60);
        System.out.println(authenticationTokenService.parseToken(token).getIssuedDate());
        String activationLink;
        String localSubject;
        String localBody;

        String serverRoot = this.reverseProxy;
        if (serverRoot.length() == 0) {
            serverRoot = request.getRequestURL().substring(0, request.getRequestURL().length() - "/ws/users".length());
        }

        if (returnUrl != null && !returnUrl.isEmpty()) {
            activationLink = serverRoot + returnUrl.replace("{token}", token);
            localSubject = messages.getString(subject);
            localBody = MessageFormat.format(messages.getString(body), activationLink, activationLink);
        } else {
            localSubject = "AREN API token";
            localBody = token;
        }

        mailingService.sendMail("noreply@aren.fr", user.getEmail(), localSubject, localBody);
    }

    /**
     * Mark a User as being activated after having clik in the mail link
     *
     */
    @GET
    @Path("activate")
    @RolesAllowed({"USER"})
    public void activateUser() {
        getUser().setActive(true);
        getService().edit(getUser());
        getService().invalidateToken(getUser());
    }

    /**
     * Ask for a password reset for a User
     *
     * @param identifier of the User
     */
    @POST
    @Path("resetPasswd")
    @RolesAllowed({"GUEST"})
    public void resetPasswd(String identifier) {
        User user = getService().findByUsernameOrEmail(identifier);
        if (user != null && user.isActive()) {
            try {
                sendLink(user, "mail_reset_password_subject", "mail_reset_password_body");
            } catch (MessagingException ex) {
                throw new RuntimeException();
            }
        }
        // else nothing happend, it avoids someone to bruteforce user
    }

    /**
     * Mark a User as being removed without deleting its associated datas
     *
     * @param userId to remove
     */
    @Override
    public void remove(Long userId) {
        User toDelete = getService().find(userId);
        notificationService.removeAllByUser(userId);
        getService().hide(userId);
        super.edit(userId, toDelete);
    }

    /**
     * Remove an User with its associated datas
     *
     * @param userId to remove
     */
    @DELETE
    @Path("{id}/permanent")
    @RolesAllowed({"SUPERADMIN"})
    public void permanentRemove(@PathParam("id") Long userId) {
        User entity = find(userId);
        safetyPreDeletion(entity);
        notificationService.removeAllByUser(userId);
        getService().remove(entity);
    }

    /**
     * Change User's password
     *
     * @param changePasswd
     * @param token
     */
    @PUT
    @Path("passwd")
    @RolesAllowed({"USER"})
    public void changePassword(ChangePassword changePasswd, @QueryParam("token") String token) {
        // Mail token are 24h long, login token are 1 year long
        // If this is a token from a mail, then it comes from a password reset requests
        // so we do not check the old password
        if (token != null && !token.isEmpty()) {
            AuthenticationTokenDetails authToken = authenticationTokenService.parseToken(token);
            if (authToken.getIssuedDate().plusSeconds(24 * 3600).isEqual(authToken.getExpirationDate())) {
                userService.changePassword(getUser(), changePasswd.getNewPassword());
                getService().invalidateToken(getUser());
                return;
            }
        }
        authentificationService.validateCredentials(getUser().getUsername(), changePasswd.getPassword());
        userService.changePassword(getUser(), changePasswd.getNewPassword());
        getService().invalidateToken(getUser());
    }

    /**
     *
     * @return
     */
    @Override
    @RolesAllowed({"ADMIN", "MODO"})
    public Set<User> findAll() {

        boolean alone = this.standalone != null;
        return getService().findAll(alone);
    }

    /**
     * Find the User the is currently authentificated
     *
     * @return
     */
    @GET
    @Path("me")
    @RolesAllowed({"GUEST"})
    public User getAuthenticatedUser() {

        return getUser();
    }

    /**
     * Check the existance of an User by its username or mail
     *
     * @param usernameOrMail
     * @return
     */
    @GET
    @Path("test")
    @RolesAllowed({"GUEST"})
    public boolean exists(@QueryParam("identifier") String usernameOrMail) {
        return getService().findByUsernameOrEmail(usernameOrMail) != null;
    }
}
