package fr.lirmm.aren.model;

import java.io.Serializable;
import java.util.TreeSet;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Where;

/**
 * Model for Institutions with anotations for storage and serialization
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Entity
@Table(name = "institutions")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Institution.class)
public class Institution extends AbstractEntEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4125013973438656239L;

    @Size(max = 255)
    @Column(name = "type")
    private String type;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 255)
    @Column(name = "academy")
    private String academy;

    @OneToMany(mappedBy = "institution")
    @Where(clause = "is_active = true")
    @SortNatural
    private SortedSet<User> users = new TreeSet<User>();

    @OneToMany(mappedBy = "institution")
    @SortNatural
    private SortedSet<Team> teams = new TreeSet<Team>();

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
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public String getAcademy() {
        return academy;
    }

    /**
     *
     * @param academy
     */
    public void setAcademy(String academy) {
        this.academy = academy;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public SortedSet<User> getUsers() {
        return users;
    }

    /**
     *
     * @param users
     */
    public void setUsers(SortedSet<User> users) {
        this.users = users;
    }

    /**
     *
     * @return
     */
    public SortedSet<Team> getTeams() {
        return teams;
    }

    /**
     *
     * @param teams
     */
    public void setTeams(SortedSet<Team> teams) {
        this.teams = teams;
    }

    /**
     *
     * @param team
     */
    public void addTeam(Team team) {
        team.setInstitution(this);
        this.teams.add(team);

    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEditable() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRemovable() {
        return true;
    }

}
