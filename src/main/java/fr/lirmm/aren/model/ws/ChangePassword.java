package fr.lirmm.aren.model.ws;

/**
 *
 * @author florent
 */
public class ChangePassword {

    private String password;
    private String newPassword;

    /**
     *
     */
    public ChangePassword() {
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
    public String getNewPassword() {
        return newPassword;
    }

    /**
     *
     * @param newPassword
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
