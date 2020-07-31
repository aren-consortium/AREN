package fr.lirmm.aren.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;

import fr.lirmm.aren.producer.Configurable;
import fr.lirmm.aren.exception.AuthenticationException;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.security.PasswordEncoder;

/**
 * Component for validating user credentials.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class AuthentificationService {

    @Inject
    private UserService userService;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    @Configurable("cas.server-login-rest")
    private String casUrl;

    /**
     * Validate username and password.
     *
     * @param username
     * @param password
     * @return
     */
    public User validateCredentials(String username, String password) {

        User user = userService.findByUsernameOrEmail(username);
        if (user == null) {
            throw AuthenticationException.BAD_CREDENTIALS();
        }

        if (!user.isActive()) {
            // User is not active
            throw AuthenticationException.INACTIVE_USER(username);
        }

        if (!passwordEncoder.checkPassword(password, user.getPassword())) {
            //&& !checkCasLogin(username, password)) {
            // Invalid password
            throw AuthenticationException.BAD_CREDENTIALS();
        }

        return user;
    }

    /**
     *
     * @param username
     * @param password
     * @return
     *
     * This is a trick to check if the username:password exists in the CAS
     * service It is then used to login the user, but doesn't log it to the
     * whole CAS system
     */
    private boolean checkCasLogin(String username, String password) {
        String success = "A_KEY_TO_CHECK_IF_THE_RESPONSE_IS_GOOD";

        Client client = ClientBuilder.newClient();

        MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
        params.add("auth_mode", "BASIC");
        params.add("orig_url", success);
        params.add("user", username);
        params.add("password", password);

        Response response = client.target(casUrl)
                .property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
                .request(MediaType.APPLICATION_FORM_URLENCODED)
                .post(Entity.form(params));

        return response.getLocation().toString().equals(success);
    }
}
