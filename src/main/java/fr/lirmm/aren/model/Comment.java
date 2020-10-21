package fr.lirmm.aren.model;

import java.io.Serializable;
import java.util.TreeSet;
import java.util.SortedSet;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fr.lirmm.aren.model.Comment.Hypostase;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.persistence.Convert;
import org.hibernate.annotations.SortNatural;

import org.hibernate.annotations.Type;

/**
 * Model for Comments with anotations for storage and serialization
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Entity
@Table(name = "comments")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Comment.class)
public class Comment extends AbstractOwnedEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5278516622163143823L;

    /**
     *
     */
    public enum Opinion {

        /**
         *
         */
        FOR,
        /**
         *
         */
        NEUTRAL,
        /**
         *
         */
        AGAINST
    }

    /**
     *
     */
    public enum Hypostase {

        /**
         *
         */
        EXPLANATION,
        /**
         *
         */
        LAW,
        /**
         *
         */
        PRINCIPLE,
        /**
         *
         */
        THEORY,
        /**
         *
         */
        BELIEF,
        /**
         *
         */
        CONJECTURE,
        /**
         *
         */
        HYPOTHESIS,
        /**
         *
         */
        AXIOM,
        /**
         *
         */
        DEFINITION,
        /**
         *
         */
        QUALITATIVE,
        /**
         *
         */
        VARIABLE,
        /**
         *
         */
        OBJECT,
        /**
         *
         */
        EVENT,
        /**
         *
         */
        PHENOMENON,
        /**
         *
         */
        DATA,
        /**
         *
         */
        MODE,
        /**
         *
         */
        DOMAIN,
        /**
         *
         */
        QUANTITATIVE,
        /**
         *
         */
        VARIATION,
        /**
         *
         */
        VARIANCE,
        /**
         *
         */
        APPROXIMATION,
        /**
         *
         */
        VALUE,
        /**
         *
         */
        CLUE,
        /**
         *
         */
        INVARIANT,
        /**
         *
         */
        DIMENSION,
        /**
         *
         */
        STRUCTURAL,
        /**
         *
         */
        STRUCTURE,
        /**
         *
         */
        METHOD,
        /**
         *
         */
        FORMALISM,
        /**
         *
         */
        CLASSIFICATION,
        /**
         *
         */
        PARADIGME,
        /**
         *
         */
        DIFFICULTY,
        /**
         *
         */
        APORIA,
        /**
         *
         */
        PARADOXE,
        /**
         *
         */
        PROBLEM;
    }

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "reformulation")
    private String reformulation;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "argumentation")
    private String argumentation;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "selection")
    private String selection;

    @Column(name = "start_container")
    private String startContainer;

    @Column(name = "start_offset")
    private Long startOffset;

    @Column(name = "end_container")
    private String endContainer;

    @Column(name = "end_offset")
    private Long endOffset;

    @Basic(optional = false)
    @NotNull
    @Column(name = "moderated")
    private boolean moderated = false;

    @Basic(optional = false)
    @NotNull
    @Column(name = "signaled")
    private boolean signaled = false;

    @OneToMany(mappedBy = "parent")
    @SortNatural
    private SortedSet<Comment> comments = new TreeSet<>();

    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", updatable = false)
    @ManyToOne
    private Comment parent;

    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "debate_id", referencedColumnName = "id", updatable = false)
    @ManyToOne(optional = false)
    private Debate debate;

    @Enumerated(EnumType.STRING)
    @Column(name = "opinion", updatable = false)
    private Opinion opinion;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "hypostases")
    private String hypostases;

    @Column(name = "tags", length = 1023)
    @Convert(converter = TagSet.Converter.class)
    private TagSet tags = new TagSet();

    @Column(name = "proposed_tags", length = 1023)
    @Convert(converter = TagSet.Converter.class)
    private TagSet proposedTags = new TagSet();

    /**
     *
     * @return
     */
    public String getReformulation() {
        return reformulation;
    }

    /**
     *
     * @param reformulation
     */
    public void setReformulation(String reformulation) {
        this.reformulation = reformulation;
    }

    /**
     *
     * @return
     */
    public String getArgumentation() {
        return argumentation;
    }

    /**
     *
     * @param argumentation
     */
    public void setArgumentation(String argumentation) {
        this.argumentation = argumentation;
    }

    /**
     *
     * @return
     */
    public String getSelection() {
        return selection;
    }

    /**
     *
     * @param selection
     */
    public void setSelection(String selection) {
        this.selection = selection;
    }

    /**
     *
     * @return
     */
    public String getStartContainer() {
        return startContainer;
    }

    /**
     *
     * @param startContainer
     */
    public void setStartContainer(String startContainer) {
        this.startContainer = startContainer;
    }

    /**
     *
     * @return
     */
    public Long getStartOffset() {
        return startOffset;
    }

    /**
     *
     * @param startOffset
     */
    public void setStartOffset(Long startOffset) {
        this.startOffset = startOffset;
    }

    /**
     *
     * @return
     */
    public String getEndContainer() {
        return endContainer;
    }

    /**
     *
     * @param endContainer
     */
    public void setEndContainer(String endContainer) {
        this.endContainer = endContainer;
    }

    /**
     *
     * @return
     */
    public Long getEndOffset() {
        return endOffset;
    }

    /**
     *
     * @param endOffset
     */
    public void setEndOffset(Long endOffset) {
        this.endOffset = endOffset;
    }

    /**
     *
     * @return
     */
    public boolean isModerated() {
        return moderated;
    }

    /**
     *
     * @param moderated
     */
    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    /**
     *
     * @return
     */
    public boolean isSignaled() {
        return signaled;
    }

    /**
     *
     * @param signaled
     */
    public void setSignaled(boolean signaled) {
        this.signaled = signaled;
    }

    /**
     *
     * @return
     */
    public Opinion getOpinion() {
        return opinion;
    }

    /**
     *
     * @param opinion
     */
    public void setOpinion(Opinion opinion) {
        this.opinion = opinion;
    }

    /**
     *
     * @return
     */
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
    public Comment getParent() {
        return parent;
    }

    /**
     *
     * @param parent
     */
    public void setParent(Comment parent) {
        this.parent = parent;
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
    public SortedSet<Hypostase> getHypostases() {
        if (this.hypostases != null && this.hypostases.length() > 0) {
            return Arrays.stream(this.hypostases.split(","))
                    .map(Hypostase::valueOf)
                    .collect(Collectors.toCollection(() -> new TreeSet<Hypostase>()));
        } else {
            return new TreeSet<Hypostase>();
        }

    }

    /**
     *
     * @param hypostases
     */
    public void setHypostases(SortedSet<Hypostase> hypostases) {
        this.hypostases = hypostases.stream()
                .map(Hypostase::name)
                .collect(Collectors.joining(","));
    }

    /**
     *
     * @return
     */
    public TagSet getProposedTags() {
        return proposedTags;
    }

    /**
     *
     * @param proposedTags
     */
    public void setProposedTags(TagSet proposedTags) {
        this.proposedTags = proposedTags;
    }

    /**
     *
     * @return
     */
    public TagSet getTags() {
        return tags;
    }

    /**
     *
     * @param tags
     */
    public void setTags(TagSet tags) {
        this.tags = tags;
    }

    /**
     *
     */
    public void removeUnusedTags() {
        TagSet tagToRemove = new TagSet();
        for (TagSet.Tag tag : this.getProposedTags()) {
            if (tag.isNegative() && !tags.contains(tag)) {
                tagToRemove.add(tag);
            }
        }
        this.getProposedTags().removeAll(tagToRemove);
    }

}
