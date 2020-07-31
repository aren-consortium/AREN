package fr.lirmm.aren.model;

import java.io.Serializable;
import java.util.Date;
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

/**
 * Model for Categories with anotations for storage and serialization
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Entity
@Table(name = "categories")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Category.class)
public class Category extends AbstractEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4359639152649086413L;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 2083)
    @Column(name = "picture")
    private String picture;

    @OneToMany(mappedBy = "category")
    @SortNatural
    private SortedSet<Document> documents = new TreeSet<>();

    @Column(name = "debates_count")
    private Integer debatesCount = 0;

    @Column(name = "documents_count")
    private Integer documentsCount = 0;

    @Column(name = "last_comment_date")
    private Date lastCommentDate;

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
    public String getPicture() {
        return picture;
    }

    /**
     *
     * @param picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public SortedSet<Document> getDocuments() {
        return documents;
    }

    /**
     *
     * @param documents
     */
    public void setDocuments(SortedSet<Document> documents) {
        this.documents = documents;
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
    public Date getLastCommentDate() {
        return lastCommentDate;
    }

    /**
     *
     * @param lastCommentDate
     */
    public void setLastCommentDate(Date lastCommentDate) {
        this.lastCommentDate = lastCommentDate;
    }

    /**
     *
     * @return
     */
    public Integer getDocumentsCount() {
        return documentsCount;
    }

    /**
     *
     * @param documentsCount
     */
    public void setDocumentsCount(Integer documentsCount) {
        this.documentsCount = documentsCount;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRemovable() {
        return documentsCount == 0;
    }

}
