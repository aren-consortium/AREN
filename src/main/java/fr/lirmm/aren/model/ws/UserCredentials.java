package fr.lirmm.aren.model.ws;

/**
 * API model for the user credentials.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
public class UserCredentials {

    private String username;
    private String password;
    private boolean rememberMe;

    /**
     *
     */
    public UserCredentials() {

    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     */
    public boolean isRememberMe() {
        return rememberMe;
    }

    /**
     *
     * @param rememberMe
     */
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
