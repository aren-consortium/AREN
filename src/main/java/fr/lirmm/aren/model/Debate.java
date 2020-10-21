package fr.lirmm.aren.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.ZonedDateTime;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.FetchType;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Where;

/**
 * Model for Debates with anotations for storage and serialization
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Entity
@Table(name = "debates")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Debate.class)
public class Debate extends AbstractOwnedEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1862506368700355733L;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Column(name = "closed")
    private ZonedDateTime closed;

    @Column(name = "is_active")
    private boolean active = true;

    @JoinTable(name = "debates_teams",
            joinColumns = {
                @JoinColumn(name = "debate_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "team_id", referencedColumnName = "id")})
    @ManyToMany
    @SortNatural
    private SortedSet<Team> teams = new TreeSet<>();

    @JoinTable(name = "debates_guests",
            joinColumns = {
                @JoinColumn(name = "debate_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    @Where(clause = "is_active = true")
    @SortNatural
    private SortedSet<User> guests = new TreeSet<>();

    @OneToMany(mappedBy = "debate")
    @SortNatural
    private SortedSet<Comment> comments = new TreeSet<>();

    @JoinColumn(name = "document_id", referencedColumnName = "id", updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Document document;

    @Column(name = "comments_count")
    private Integer commentsCount = 0;

    @Column(name = "comments_count_for")
    private Integer commentsCountFor = 0;

    @Column(name = "comments_count_against")
    private Integer commentsCountAgainst = 0;

    @Column(name = "last_comment_date")
    private ZonedDateTime lastCommentDate;

    @Column(name = "with_hypostases")
    private boolean withHypostases = false;

    @Column(name = "reformulation_check")
    private boolean reformulationCheck = true;

    @Column(name = "idfix_link")
    private boolean idfixLink = false;

    @Column(name = "open_public")
    private boolean openPublic = false;

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
    public ZonedDateTime getClosed() {
        return closed;
    }

    /**
     *
     * @param closed
     */
    public void setClosed(ZonedDateTime closed) {
        this.closed = closed;
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
        this.teams.add(team);
        team.getDebates().add(this);
    }

    /**
     *
     * @param team
     */
    public void removeTeam(Team team) {
        this.teams.remove(team);
        team.getDebates().remove(this);
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public SortedSet<User> getGuests() {
        return guests;
    }

    /**
     *
     * @param guests
     */
    public void setGuests(SortedSet<User> guests) {
        this.guests = guests;
    }

    /**
     *
     * @param guest
     */
    public void addGuest(User guest) {
        this.guests.add(guest);
        guest.getInvitedDebates().add(this);
    }

    /**
     *
     * @param guest
     */
    public void removeGuest(User guest) {
        this.guests.remove(guest);
        guest.getInvitedDebates().remove(this);
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
    public Document getDocument() {
        return document;
    }

    /**
     *
     * @param document
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     *
     * @return
     */
    public Integer getCommentsCount() {
        return commentsCount;
    }

    /**
     *
     * @return
     */
    public Integer getCommentsCountFor() {
        return commentsCountFor;
    }

    /**
     *
     * @return
     */
    public Integer getCommentsCountAgainst() {
        return commentsCountAgainst;
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getLastCommentDate() {
        return lastCommentDate;
    }

    /**
     *
     * @return
     */
    public boolean isWithHypostases() {
        return withHypostases;
    }

    /**
     *
     * @param withHypostases
     */
    public void setWithHypostases(boolean withHypostases) {
        this.withHypostases = withHypostases;
    }

    /**
     *
     * @return
     */
    public boolean isReformulationCheck() {
        return reformulationCheck;
    }

    /**
     *
     * @param reformulationCheck
     */
    public void setReformulationCheck(boolean reformulationCheck) {
        this.reformulationCheck = reformulationCheck;
    }

    /**
     *
     * @return
     */
    public boolean isIdfixLink() {
        return idfixLink;
    }

    /**
     *
     * @param idfixLink
     */
    public void setIdfixLink(boolean idfixLink) {
        this.idfixLink = idfixLink;
    }

    /**
     *
     * @return
     */
    public boolean isOpenPublic() {
        return openPublic;
    }

    /**
     *
     * @param openPublic
     */
    public void setOpenPublic(boolean openPublic) {
        this.openPublic = openPublic;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRemovable() {
        return comments.isEmpty();
    }

}
