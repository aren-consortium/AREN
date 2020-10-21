package fr.lirmm.aren.model;

import java.io.Serializable;
import java.util.TreeSet;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.JoinTable;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.SortNatural;

/**
 * Model for Users with anotations for storage and serialization
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Entity
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = User.class)
@Filters({
    @Filter(name = "isActive", condition = "active = true")
})
public class User extends AbstractEntEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 9143251821521688592L;

    /**
     *
     */
    public enum Authority {

        /**
         *
         */
        DELETED,
        /**
         *
         */
        GUEST,
        /**
         *
         */
        USER,
        /**
         *
         */
        MODO,
        /**
         *
         */
        ADMIN,
        /**
         *
         */
        SUPERADMIN
    }

    @Size(max = 255)
    @Column(name = "username", unique = true)
    private String username;

    @Size(max = 255)
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 255)
    @Column(name = "last_name")
    private String lastName;

    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 255)
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "last_login")
    private ZonedDateTime lastLogin;

    @JsonIgnore
    @Column(name = "token_validity")
    private ZonedDateTime tokenValidity = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @JsonIgnore
    @Column(name = "is_active")
    private boolean active = true;

    @ManyToMany(mappedBy = "guests")
    @SortNatural
    private SortedSet<Debate> invitedDebates = new TreeSet<>();

    @OneToMany(mappedBy = "owner")
    @SortNatural
    private SortedSet<Comment> comments = new TreeSet<>();

    @OneToMany(mappedBy = "owner")
    @SortNatural
    private SortedSet<Debate> createdDebates = new TreeSet<>();

    @JoinColumn(name = "institution_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Institution institution;

    @JoinTable(name = "teams_users",
            joinColumns = {
                @JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "team_id", referencedColumnName = "id")})
    @ManyToMany
    @SortNatural
    private SortedSet<Team> teams = new TreeSet<>();

    @OneToMany(mappedBy = "owner")
    @SortNatural
    private SortedSet<Notification> notifications = new TreeSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false)
    private Authority authority = Authority.GUEST;

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getLastLogin() {
        return lastLogin;
    }

    /**
     *
     * @param lastLogin
     */
    public void setLastLogin(ZonedDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getTokenValidity() {
        return tokenValidity;
    }

    /**
     *
     * @param tokenValidity
     */
    public void setTokenValidity(ZonedDateTime tokenValidity) {
        this.tokenValidity = tokenValidity;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     *
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public SortedSet<Debate> getInvitedDebates() {
        return invitedDebates;
    }

    /**
     *
     * @param invitedDebates
     */
    public void setInvitedDebates(SortedSet<Debate> invitedDebates) {
        this.invitedDebates = invitedDebates;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public SortedSet<Comment> getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     */
    public void setComments(SortedSet<Comment> comments) {
        this.comments = comments;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public SortedSet<Debate> getCreatedDebates() {
        return createdDebates;
    }

    /**
     *
     * @param createdDebates
     */
    public void setCreatedDebates(SortedSet<Debate> createdDebates) {
        this.createdDebates = createdDebates;
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
        teams.add(team);
        team.getUsers().add(this);
    }

    /**
     *
     * @param team
     */
    public void removeTeams(Team team) {
        teams.remove(team);
        team.getUsers().remove(this);
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public SortedSet<Notification> getNotifications() {
        return notifications;
    }

    /**
     *
     * @param notifications
     */
    public void setNotifications(SortedSet<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     *
     * @return
     */
    public Authority getAuthority() {
        return authority;
    }

    /**
     *
     * @param authority
     */
    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    /**
     *
     * @param other
     * @return
     */
    public boolean hasSameOrMoreRightThan(User other) {
        return authority.compareTo(other.getAuthority()) >= 0;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEditable() {
        return institution == null;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRemovable() {
        return this.getInstitution().getId() == 0L && this.getComments().size() == 0;
    }

    /**
     *
     * @param auth
     * @return
     */
    public boolean is(Authority auth) {
        if (this.getAuthority() == null) {
            return Authority.GUEST.compareTo(auth) >= 0;
        }
        return this.getAuthority().compareTo(auth) >= 0;
    }

    /**
     *
     * @param auth
     * @return
     */
    public boolean is(String auth) {
        return this.is(Authority.valueOf(auth));
    }
}
