package fr.lirmm.aren.exception;

import java.util.HashMap;

/**
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
public class AbstractException extends RuntimeException {

    /**
     *
     */
    protected HashMap<String, String> details = new HashMap<String, String>();

    /**
     *
     * @param string
     */
    public AbstractException(String string) {
        super(string);
    }

    /**
     *
     * @return
     */
    public HashMap<String, String> getDetails() {
        return details;
    }
}
