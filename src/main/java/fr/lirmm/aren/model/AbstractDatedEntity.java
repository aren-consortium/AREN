package fr.lirmm.aren.model;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Model that olds a creation date
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@MappedSuperclass
public abstract class AbstractDatedEntity extends AbstractEntity {

    @Column(name = "created", nullable = false, updatable = false)
    private ZonedDateTime created = ZonedDateTime.now();

    /**
     *
     * @return
     */
    public ZonedDateTime getCreated() {
        return created;
    }

    /**
     *
     * @param created
     */
    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
