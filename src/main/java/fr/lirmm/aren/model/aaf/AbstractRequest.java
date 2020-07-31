package fr.lirmm.aren.model.aaf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Part of ficAlimMENESR object, for XML serialization.
 * 
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@XmlRootElement
public class AbstractRequest {

    @XmlElementWrapper
    @XmlElements({
        @XmlElement(name = "attr", type = Attribute.class)
    })
    private AttributeList operationalAttributes;

    @XmlElement
    private Identifier identifier;

    /**
     *
     * @return
     */
    @XmlTransient
    public AttributeList getOperationalAttributes() {
        return operationalAttributes;
    }

    /**
     *
     * @param operationalAttributes
     */
    public void setOperationalAttributes(AttributeList operationalAttributes) {
        this.operationalAttributes = operationalAttributes;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     *
     * @param identifier
     */
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public String getId() {
        return identifier.getId();
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public AttributeList getAttributes() {
        return new AttributeList();
    }
}
