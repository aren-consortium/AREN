package fr.lirmm.aren.model;

import java.io.Serializable;
import java.util.TreeSet;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Type;

/**
 * Model for Documents with anotations for storage and serialization
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Entity
@Table(name = "documents")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Document.class)
public class Document extends AbstractDatedEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8507685898376675066L;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 255)
    @Column(name = "author")
    private String author;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "content")
    private String content;

    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Category category;

    @OneToMany(mappedBy = "document")
    @SortNatural
    private SortedSet<Debate> debates = new TreeSet<>();

    @Column(name = "debates_count")
    private Integer debatesCount = 0;

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
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @return
     */
    public Category getCategory() {
        return category;
    }

    /**
     *
     * @param category
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public SortedSet<Debate> getDebates() {
        return debates;
    }

    /**
     *
     * @param debates
     */
    public void setDebates(SortedSet<Debate> debates) {
        this.debates = debates;
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
     * @param debatesCount
     */
    public void setDebatesCount(Integer debatesCount) {
        this.debatesCount = debatesCount;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEditable() {
        return debates.isEmpty();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRemovable() {
        return debates.isEmpty();
    }
}
