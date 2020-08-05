package fr.lirmm.aren.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Model that olds an ent id (part of the AAF)
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@MappedSuperclass
public abstract class AbstractEntEntity extends AbstractDatedEntity {

    @Column(name = "ent_id", updatable = false, unique = true)
    private String entId;

    /**
     *
     * @return
     */
    public String getEntId() {
        return entId;
    }

    /**
     *
     * @param entId
     */
    public void setEntId(String entId) {
        this.entId = entId;
    }

    /**
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (object.getClass() != this.getClass()) {
            return false;
        }
        AbstractEntEntity other = (AbstractEntEntity) object;
        if (this.getEntId() != null && other.getEntId() != null) {
            return this.getEntId().equals(other.getEntId());
        } else if (this.getId() != null && other.getId() != null) {
            return this.getId().equals(other.getId());
        }
        return false;
    }
}
