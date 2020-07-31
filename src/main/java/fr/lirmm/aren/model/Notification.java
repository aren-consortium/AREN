package fr.lirmm.aren.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fr.lirmm.aren.model.ws.Message;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/**
 * Model for Notifications with anotations for storage and serialization
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Entity
@Table(name = "notifications")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Notification.class)
public class Notification extends AbstractOwnedEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6593370506138662915L;

    @Lob
    private Message content;

    @Column(name = "is_unread")
    private boolean unread = true;

    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "debate_id", referencedColumnName = "id", updatable = false)
    @ManyToOne()
    private Debate debate;

    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "comment_id", referencedColumnName = "id", updatable = false)
    @ManyToOne()
    private Comment comment;

    /**
     *
     */
    public Notification() {
    }

    private Notification(User owner, Debate debate, Comment comment) {
        this.owner = owner;
        this.debate = debate;
        this.comment = comment;
    }

    /**
     *
     * @param comment
     * @return
     */
    public static Notification COMMENT_MODERATED(Comment comment) {
        Notification n = new Notification(comment.getOwner(), comment.getDebate(), comment);
        n.content = new Message("comment_moderated");
        n.content.addDetail("debateName", comment.getDebate().getDocument().getName());
        return n;
    }

    /**
     *
     * @param modo
     * @param comment
     * @return
     */
    public static Notification COMMENT_SINGNALED(User modo, Comment comment) {
        Notification n = new Notification(modo, comment.getDebate(), comment);
        n.content = new Message("comment_signaled");
        n.content.addDetail("debateName", comment.getDebate().getDocument().getName());
        return n;
    }

    /**
     *
     * @param owner
     * @param comment
     * @return
     */
    public static Notification COMMENT_ANSWERED(User owner, Comment comment) {
        Notification n = new Notification(owner, comment.getDebate(), comment);
        n.content = new Message("comment_answered");
        n.content.addDetail("firstName", comment.getOwner().getFirstName());
        n.content.addDetail("lastName", comment.getOwner().getLastName());
        return n;
    }

    /**
     *
     * @param owner
     * @param debate
     * @return
     */
    public static Notification INVITED_TO_DEBATE(User owner, Debate debate) {
        Notification n = new Notification(owner, debate, null);
        n.content = new Message("invited_to_debate");
        n.content.addDetail("debateName", debate.getDocument().getName());
        return n;
    }

    /**
     *
     * @param owner
     * @param debate
     * @param team
     * @return
     */
    public static Notification TEAM_ADDED_TO_DEBATE(User owner, Debate debate, Team team) {
        Notification n = new Notification(owner, debate, null);
        n.content = new Message("team_added_to_debate");
        n.content.addDetail("debateName", debate.getDocument().getName());
        n.content.addDetail("teamName", team.getName());
        return n;
    }

    /**
     *
     * @return
     */
    public Message getContent() {
        return content;
    }

    /**
     *
     * @param content
     */
    public void setContent(Message content) {
        this.content = content;
    }

    /**
     *
     * @return
     */
    public boolean isUnread() {
        return unread;
    }

    /**
     *
     * @param unread
     */
    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    /**
     *
     * @return
     */
    public Debate getDebate() {
        return debate;
    }

    /**
     *
     * @param debate
     */
    public void setDebate(Debate debate) {
        this.debate = debate;
    }

    /**
     *
     * @return
     */
    public Comment getComment() {
        return comment;
    }

    /**
     *
     * @param comment
     */
    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @JsonIdentityReference(alwaysAsId = true)
    @Override
    public User getOwner() {
        return super.getOwner();
    }

}
