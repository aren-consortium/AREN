package fr.lirmm.aren.model.aaf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Specialization of AbstractRequest for Modification.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@XmlRootElement
public class ModifyRequest extends AbstractRequest {

    @XmlElementWrapper(name = "modifications")
    @XmlElements({
        @XmlElement(name = "modification", type = Attribute.class)
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
