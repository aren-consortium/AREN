package fr.lirmm.aren.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Where;

/**
 * Model for Teams with anotations for storage and serialization
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Entity
@Table(name = "teams")
@NamedQueries({
    @NamedQuery(name = "Team.findByEntId", query = "SELECT t FROM Team t WHERE t.entId = :entId")})

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Team.class)
public class Team extends AbstractEntEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -286738505010538851L;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Column(name = "is_community", updatable = false)
    private boolean community = false;

    @JoinColumn(name = "institution_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Institution institution;

    @ManyToMany(mappedBy = "teams")
    private Set<Debate> debates = new HashSet<>();

    @ManyToMany(mappedBy = "teams")
    @Where(clause = "is_active = true")
    private Set<User> users = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "debates_count")
    private Integer debatesCount = 0;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "users_count")
    private Integer usersCount = 0;

    /**
     *
     * @return
     */
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
    public boolean isCommunity() {
        return community;
    }

    /**
     *
     * @param community
     */
    public void setCommunity(boolean community) {
        this.community = community;
    }

    /**
     *
     * @return
     */
    public Institution getInstitution() {
        return institution;
    }

    /**
     *
     * @param institution
     */
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public Set<Debate> getDebates() {
        return debates;
    }

    /**
     *
     * @param debates
     */
    public void setDebates(Set<Debate> debates) {
        this.debates = debates;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public Set<User> getUsers() {
        return users;
    }

    /**
     *
     * @param users
     */
    public void setUsers(Set<User> users) {
        this.users = users;
    }

    /**
     *
     * @param user
     */
    public void addUser(User user) {
        this.users.add(user);
        user.getTeams().add(this);
    }

    /**
     *
     * @param user
     */
    public void removeUser(User user) {
        this.users.remove(user);
        user.getTeams().remove(this);
    }

    /**
     *
     * @return
     */
    public Integer getDebatesCount() {
        return debatesCount;
    }

    /**
     *
     * @return
     */
    public Integer getUsersCount() {
        return usersCount;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEditable() {
        return getEntId() == null;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRemovable() {
        return getEntId() == null;
    }

}
