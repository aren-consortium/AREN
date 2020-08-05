package fr.lirmm.aren.model.ws;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import fr.lirmm.aren.model.Comment;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author florent
 */
public class Scrap implements Comparable {

    /**
     *
     */
    public static int ALL_BEFORE = -3;

    /**
     *
     */
    public static int OVER_BEFORE = -2;

    /**
     *
     */
    public static int COVERS = -1;

    /**
     *
     */
    public static int EQUALS = 0;

    /**
     *
     */
    public static int BELONGS = 1;

    /**
     *
     */
    public static int OVER_AFTER = 2;

    /**
     *
     */
    public static int ALL_AFTER = 3;

    private String startContainer;

    private Long startOffset;

    private String endContainer;

    private Long endOffset;

    @JsonIdentityReference(alwaysAsId = true)
    private Set<Comment> comments = new HashSet<>();

    /**
     *
     */
    public Scrap() {
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
    public Set<Comment> getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     */
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    private int comparePosition(String[] pos1, String[] pos2) {
        if (pos1.equals(pos2)) {
            return 0;
        }
        int len = Math.max(pos1.length, pos2.length);
        for (int i = 0; i < len; i++) {
            if (i >= pos1.length || i < pos2.length && Integer.parseInt(pos1[i]) < Integer.parseInt(pos2[i])) {
                return -1;
            } else if (i >= pos2.length || i < pos1.length && Integer.parseInt(pos1[i]) > Integer.parseInt(pos2[i])) {
                return 1;
            }
        }
        return 0;
    }

    /**
     *
     * @param other
     * @return
     */
    public boolean equals(Scrap other) {
        return this.startContainer.equals(other.startContainer)
                && this.startOffset.equals(other.startOffset)
                && this.endContainer.equals(other.endContainer)
                && this.endOffset.equals(other.endOffset);
    }

    /**
     *
     * @param t
     * @return
     */
    @Override
    public int compareTo(Object t) {
        if (!(t instanceof Scrap)) {
            throw new IllegalArgumentException("Compared object should be a Scrap");
        }
        Scrap other = (Scrap) t;

        int StS = comparePosition(
                (this.startContainer + "/" + this.startOffset).split("/"),
                (other.startContainer + "/" + other.startOffset).split("/"));
        int StE = comparePosition(
                (this.startContainer + "/" + this.startOffset).split("/"),
                (other.endContainer + "/" + other.endOffset).split("/"));
        int EtS = comparePosition(
                (this.endContainer + "/" + this.endOffset).split("/"),
                (other.startContainer + "/" + other.startOffset).split("/"));
        int EtE = comparePosition(
                (this.endContainer + "/" + this.endOffset).split("/"),
                (other.endContainer + "/" + other.endOffset).split("/"));

        if (StS == 0 && EtE == 0) {
            return Scrap.EQUALS;
        }
        if (EtS <= 0) {
            return Scrap.ALL_BEFORE;
        }
        if (StE >= 0) {
            return Scrap.ALL_AFTER;
        }
        if (StS <= 0 && EtE >= 0) {
            return Scrap.COVERS;
        }
        if (StS >= 0 && EtE <= 0) {
            return Scrap.BELONGS;
        }
        if (StS <= 0) {
            return Scrap.OVER_BEFORE;
        }
        if (StS >= 0) {
            return Scrap.OVER_AFTER;
        }
        throw new IllegalArgumentException("Imposible possitioning");
    }

    /**
     *
     * @return
     */
    public String toString() {
        return this.startContainer + "/" + this.startOffset + " > " + this.endContainer + "/" + this.endOffset;
    }
}
