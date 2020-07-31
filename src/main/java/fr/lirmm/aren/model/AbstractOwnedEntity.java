package fr.lirmm.aren.model;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Model that olds a uniq specific User
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@MappedSuperclass
public abstract class AbstractOwnedEntity extends AbstractDatedEntity {

    /**
     *
     */
    @JoinColumn(name = "owner_id", referencedColumnName = "id", updatable = false)
    @ManyToOne(optional = false)
    protected User owner;

    /**
     *
     * @return
     */
    public User getOwner() {
        return owner;
    }

    /**
     *
     * @param owner
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
