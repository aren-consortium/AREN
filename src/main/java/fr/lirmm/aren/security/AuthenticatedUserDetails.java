package fr.lirmm.aren.security;

import java.security.Principal;

import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.User.Authority;

/**
 * {@link Principal} implementation with a set of {@link Authority}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
public final class AuthenticatedUserDetails implements Principal {

    private final User user;

    /**
     *
     * @param user
     */
    public AuthenticatedUserDetails(User user) {
        this.user = user;
    }

    /**
     *
     * @return the user's authority
     */
    public Authority getAuthority() {
        return user.getAuthority();
    }

    /**
     *
     * @return the username
     */
    @Override
    public String getName() {
        return user.getUsername();
    }

    /**
     *
     * @return the user object
     */
    public User getUser() {
        return user;
    }
}
