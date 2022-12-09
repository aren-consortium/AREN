package fr.lirmm.aren.service;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fr.lirmm.aren.model.Configuration;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.producer.ConfigurationProducer;

/**
 * Service that provides operations for {@link User}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class ConfigurationService extends AbstractService<Configuration> {

  @Inject
  private ConfigurationProducer configurationproducer;

  /**
   *
   */
  public ConfigurationService() {
    super(Configuration.class);
  }

  public void updateAll(Set<Configuration> configurations) {
    super.transactionBegin();
    Properties properties = this.configurationproducer.getAll();
    configurations.forEach(config -> {
      if (config.getValue() == null) {
        this.removeKey(config.getKey());
      } else if (!properties.getProperty(config.getKey()).equals(config.getValue())) {
        this.updateKey(config.getKey(), config.getValue(), false);
      }
    });
    super.commit();
    configurationproducer.loadDB();
  }

  public void removeKey(String key) {
    Configuration config = findByKey(key);
    if (config != null) {
      getEntityManager().remove(config);
    }
  }

  public void updateKey(String key, String value) {
    this.updateKey(key, value, true);
  }

  private void updateKey(String key, String value, boolean reload) {
    Configuration config = findByKey(key);
    if (config == null) {
      this.create(new Configuration(key, value));
    } else {
      super.transactionBegin();
      getEntityManager().createQuery("UPDATE Configuration "
          + "SET value = :value "
          + "WHERE key = :key")
          .setParameter("key", key)
          .setParameter("value", value)
          .executeUpdate();
      super.commit();
      getEntityManager().refresh(config);
    }
    if (reload) {
      configurationproducer.loadDB();
    }
  }

  private Configuration findByKey(String key) {
    List<Configuration> results = getEntityManager().createQuery("SELECT c "
        + "FROM Configuration c "
        + "WHERE c.key = :key", Configuration.class)
        .setParameter("key", key)
        .getResultList();

    if (results.isEmpty()) {
      return null;
    }
    return results.get(0);
  }
}
