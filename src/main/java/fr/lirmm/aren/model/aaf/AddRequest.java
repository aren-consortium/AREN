package fr.lirmm.aren.model.aaf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Specialization of AbstractRequest for Addition.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@XmlRootElement
public class AddRequest extends AbstractRequest {

    @XmlElementWrapper(name = "attributes")
    @XmlElements({
        @XmlElement(name = "attr", type = Attribute.class)
    })
    private AttributeList attributes;

    /**
     *
     * @return
     */
    @XmlTransient
    @Override
    public AttributeList getAttributes() {
        return attributes;
    }

    /**
     *
     * @param attributes
     */
    public void setAttributes(AttributeList attributes) {
        this.attributes = attributes;
    }

}
