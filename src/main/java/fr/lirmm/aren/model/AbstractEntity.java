package fr.lirmm.aren.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * Model that olds an id
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@MappedSuperclass
public abstract class AbstractEntity implements Comparable<AbstractEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    /**
     *
     */
    public AbstractEntity() {
    }

    /**
     *
     * @param l
     */
    public AbstractEntity(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    @JsonIgnore
    public boolean isEditable() {
        return true;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    @JsonIgnore
    public boolean isRemovable() {
        return true;
    }

    /**
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Merges the non null values of an object into this Useful to populate /
     * update a persisted object
     *
     * @param other
     */
    public void merge(AbstractEntity other) {

        // Only apply on same class
        if (!this.getClass().equals(other.getClass())) {
            return;
        }

        // Get all methods of the current object
        Method[] methods = this.getClass().getMethods();

        for (Method getter : methods) {
            // Only proceed for getter functions
            if (getter.getName().startsWith("get") || getter.getName().startsWith("is")) {

                // Deduce the setter name from the getter name
                String setterName = getter.getName().replaceFirst("get|is", "set");

                try {
                    // Get the actual setter method
                    Method setter = this.getClass().getMethod(setterName, getter.getReturnType());
                    // Apply the getter on the object to merge and replace the value in the actual object if it's not null
                    Object value = getter.invoke(other);

                    if (value != null) {
                        /*if (value instanceof Collection) {
                            Collection myValue = (Collection) getter.invoke(this);
                            for (Object obj : (Collection) myValue) {
                                ((Collection) value).add(obj);
                            }
                        } else if (value instanceof Map) {
                            Map myValue = (Map) getter.invoke(this);
                            for (Map.Entry<Object, Object> obj : ((Map<Object, Object>) myValue).entrySortedSet()) {
                                ((Map) value).put(obj.getKey(), obj.getValue());
                            }
                        }*/
                        //if (!(value instanceof AbstractEntity) || ((AbstractEntity) value).getId() != null) {
                        setter.invoke(this, value);
                        //}
                    }
                } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
                    // Really unexpected !
                    System.err.println(e.getMessage());
                } catch (NoSuchMethodException e) {
                    // The only suposetly possible error.
                    // It means the field is not suposed to be updated.
                }
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (object == null || this == null) {
            return false;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        AbstractEntity other = this.getClass().cast(object);
        return !((this.id == null && other.getId() != null) || (this.id != null && !this.id.equals(other.getId())));
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "fr.lirmm.aren.model." + this.getClass().getName() + " [ id=" + id + " ]";
    }

    @Override
    public int compareTo(AbstractEntity t) {
        return (int) (this.getId() - t.getId());
    }
}
