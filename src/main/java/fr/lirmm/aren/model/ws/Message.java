package fr.lirmm.aren.model.ws;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.Embeddable;

/**
 * Data transfer object that holds details about an API error.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {

    private String message;

    private HashMap<String, String> details = new HashMap<String, String>();

    /**
     *
     */
    public Message() {

    }
    
    /**
     *
     * @param string
     */
    public Message(String message) {
        this.message = message;
    }
    
    /**
     *
     * @return
     */
    public String paseMessage() {
        String result = this.message;
        details.forEach((String label, String value) -> {
            result.replace("{"+label+"}", value);
        });
        return result;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 
     * @return 
     */
    public HashMap<String, String> getDetails() {
        return details;
    }

    /**
     * 
     * @param details 
     */
    public void setDetails(HashMap<String, String> details) {
        this.details = details;
    }
    
    /**
     * 
     * @param name
     * @param value 
     */
    public void addDetail(String name, String value) {
        this.details.put(name, value);
    }
}
