package fr.lirmm.aren.security;

import javax.enterprise.context.ApplicationScoped;

import org.mindrot.jbcrypt.BCrypt;

/**
 * bcrypt password encoder.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class PasswordEncoder {

    /**
     * Hashes a password using BCrypt.
     *
     * @param plainTextPassword
     * @return the has of the given string
     */
    public String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Checks a password against a stored hash using BCrypt.
     *
     * @param plainTextPassword
     * @param hashPassword
     * @return true if the hash of the first parameter equals the the second
     * parameter
     */
    public boolean checkPassword(String plainTextPassword, String hashPassword) {
        if (null == hashPassword || !hashPassword.startsWith("$2a$")) {
            throw new RuntimeException("Hashed password is invalid");
        }
        return BCrypt.checkpw(plainTextPassword, hashPassword);
    }
}
