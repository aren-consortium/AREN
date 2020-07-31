package fr.lirmm.aren.model.ws;

/**
 * Data transfer object that holds details about an API error.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */

public class ApiErrorDetails extends Message {

    private Integer status;
    private String title;
    private String path;

    /**
     *
     */
    public ApiErrorDetails() {

    }

    /**
     *
     * @return
     */
    public Integer getStatus() {
        return status;
    }

    /**
     *
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     *
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
