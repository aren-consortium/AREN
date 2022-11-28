package fr.lirmm.aren.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Model for Configuration properties with anotations for storage and
 * serialization
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Entity
@Table(name = "configurations")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Configuration.class)
public class Configuration extends AbstractEntity implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 586734505910535351L;

  @Size(max = 255)
  @Column(name = "key", unique = true)
  private String key;

  @Size(max = 255)
  @Column(name = "value")
  private String value;

  public Configuration() {
  }

  public Configuration(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
