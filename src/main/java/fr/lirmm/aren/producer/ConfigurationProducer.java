package fr.lirmm.aren.producer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import fr.lirmm.aren.service.ConfigurationService;

/**
 * Read the <code>application.properties</code> file from the classpath and
 * produce values that can be injected with @{@link Configurable}.
 * <p>
 * It's a simple and lightweight alternative to the Apache DeltaSpike
 * Configuration Mechanism.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class ConfigurationProducer {

  @Inject
  private ConfigurationService configService;

  private Properties properties;
  private Properties dbProperties;

  /**
   * Load the defaults configuration in the application.properties
   * Then try to load the custom configuraitons in the database
   */
  @PostConstruct
  public void init() {
    this.properties = new Properties();
    this.dbProperties = new Properties();
    InputStream stream = ConfigurationProducer.class.getResourceAsStream("/application.properties");

    if (stream == null) {
      throw new RuntimeException("Cannot find application.properties configuration file.");
    }

    try {
      this.properties.load(stream);
    } catch (final IOException e) {
      throw new RuntimeException("Configuration file cannot be loaded.");
    }

    try {
      this.loadDB();
    } catch (Throwable e) {
    }
  }

  /**
   * Load the custom properties in the database
   */
  public void loadDB() {
    this.configService.findAll().forEach(config -> {
      dbProperties.setProperty(config.getKey(), config.getValue());
    });
  }

  /**
   *
   * @param ip
   * @return
   */
  @Produces
  @Configurable
  public String produceString(InjectionPoint ip) {
    if (dbProperties.containsKey(getKey(ip)))
      return dbProperties.getProperty(this.getKey(ip));
    return properties.getProperty(this.getKey(ip));
  }

  /**
   *
   * @param ip
   * @return
   */
  @Produces
  @Configurable
  public Integer produceInteger(InjectionPoint ip) {
    return Integer.valueOf(this.produceString(ip));
  }

  /**
   *
   * @param ip
   * @return
   */
  @Produces
  @Configurable
  public Long produceLong(InjectionPoint ip) {
    return Long.valueOf(this.produceString(ip));
  }

  /**
   *
   * @param ip
   * @return
   */
  @Produces
  @Configurable
  public Boolean produceBoolean(InjectionPoint ip) {
    return Boolean.valueOf(this.produceString(ip));
  }

  private String getKey(InjectionPoint ip) {
    return ip.getAnnotated().getAnnotation(Configurable.class).value();
  }
}
