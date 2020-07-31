package fr.lirmm.aren.model.aaf;

import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Part of AttributeList.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@XmlRootElement
public class Attribute {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "operation")
    private String operation;

    @XmlElement(name = "value")
    private Set<String> values;

    Attribute() {
    }

    Attribute(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public String getOperation() {
        return operation;
    }

    /**
     *
     * @param operation
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public Set<String> getValues() {
        return values;
    }

    /**
     *
     * @param values
     */
    public void setValues(Set<String> values) {
        this.values = values;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    /**
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof Attribute) {
            return this.name.equals(((Attribute) object).getName());
        } else {
            return false;
        }
    }
}
