package fr.lirmm.aren.model.aaf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Part of a Request.
 * 
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@XmlRootElement
public class Identifier {

    @XmlElement
    private String id;

    /**
     *
     * @return
     */
    @XmlTransient
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

}
