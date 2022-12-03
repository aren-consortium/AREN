package fr.lirmm.aren.producer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * CDI producer for the JPA {@link EntityManager}.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class EntityManagerProducer {

  private EntityManagerFactory factory;

  private Properties credentials;

  /**
   *
   */
  @PostConstruct
  public void init() {
    this.loadCredentials();
    try {
      this.loadFactory();
    } catch (Throwable ex) {
    }
  }

  /**
   * Loads credentioals in the ressources/applications.properties file
   */
  private void loadCredentials() {
    if (credentials == null) {
      this.credentials = new Properties();

      InputStream stream = EntityManagerProducer.class.getResourceAsStream("/database.properties");
      if (stream != null) {
        try {
          this.credentials.load(stream);
        } catch (final IOException e) {
          throw new RuntimeException("Database configuration file cannot be loaded.");
        }
      }
    }
  }

  /**
   * Create a factory with the default options stored in
   * resources/META-INF/persistence.xml
   * and the current credentials
   */
  private void loadFactory() {
    if (factory != null) {
      factory.close();
    }
    this.factory = Persistence.createEntityManagerFactory("default", credentials);
  }

  /**
   * Set and store the given credential for the database
   * 
   * @param server   : the url of the DB server
   * @param port     : the port of the DB server
   * @param name     : the name of the DB
   * @param user     : the admin user of the DB
   * @param password : the password of the admin user
   */
  public void setCredentials(String server, String port, String name, String user, String password) {
    credentials = new Properties();
    credentials.put("hibernate.connection.url", "jdbc:postgresql://" + server + ":" + port + "/" + name + "");
    credentials.put("hibernate.connection.username", user);
    credentials.put("hibernate.connection.password", password);
    credentials.put("hibernate.hbm2ddl.auto", "update");

    String path = EntityManagerProducer.class.getResource("/database.properties").getPath();
    try {
      credentials.store(new FileOutputStream(path), null);
    } catch (IOException e) {
      throw new RuntimeException("Cannot write database.properties configuration file.");
    }
    this.loadFactory();
  }

  /**
   *
   * @return
   */
  @Produces
  @Default
  @RequestScoped
  public EntityManager createRequestEntityManager() {
    return factory.createEntityManager();
  }

  /**
   *
   * @param entityManager
   */
  public void closeEntityManager(@Disposes EntityManager entityManager) {
    if (entityManager.isOpen()) {
      entityManager.close();
    }
  }

  /**
   *
   */
  @PreDestroy
  public void destroy() {
    if (factory.isOpen()) {
      factory.close();
    }
  }
}
