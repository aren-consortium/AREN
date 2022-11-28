package fr.lirmm.aren.service;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.PersistenceException;

import fr.lirmm.aren.model.Configuration;
import fr.lirmm.aren.model.User;

/**
 * Service that provides operations for {@link User}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class ConfigurationService extends AbstractService<Configuration> {

  /**
   *
   */
  public ConfigurationService() {
    super(Configuration.class);
  }

  public void updateAll(Map<String, String> properties) {
    super.transactionBegin();
    properties.forEach(this::updateKey);
    super.commit();
  }

  public void updateKey(String key, String value) {
    Configuration config = findByKey(key);
    if (config == null) {
      this.create(new Configuration(key, value));
    } else {
      config.setValue(value);
      this.edit(config);
    }
  }

  private Configuration findByKey(String key) {
    try {
      return getEntityManager().createQuery("SELECT c "
          + "FROM Config c "
          + "WHERE c.key = :key", Configuration.class)
          .setParameter("key", key)
          .getSingleResult();
    } catch (PersistenceException ex) {
      return null;
    }
  }
}
