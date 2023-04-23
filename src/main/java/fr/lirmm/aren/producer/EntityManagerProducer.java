package fr.lirmm.aren.producer;

import static fr.lirmm.aren.producer.Scope.Type.APPLICATION;
import static fr.lirmm.aren.producer.Scope.Type.REQUEST;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

  private static final String DB_CONFIG_PATH = String.format("%s/conf/aren.properties", System.getProperty("catalina.base"));

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

      try {
        credentials.load(new FileInputStream(DB_CONFIG_PATH));
      } catch (IOException e) {
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

    try {
      credentials.store(new FileOutputStream(DB_CONFIG_PATH), null);
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
  @Scope(REQUEST)
  @RequestScoped
  public EntityManager createRequestEntityManager() {
    return factory.createEntityManager();
  }

  /**
   *
   * @return
   */
  @Produces
  @Scope(APPLICATION)
  @ApplicationScoped
  public EntityManager createApplicationEntityManager() {
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