package fr.lirmm.aren.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import fr.lirmm.aren.model.Configuration;
import fr.lirmm.aren.model.Institution;
import fr.lirmm.aren.model.User;
import fr.lirmm.aren.model.User.Authority;
import fr.lirmm.aren.producer.Configurable;
import fr.lirmm.aren.producer.ConfigurationProducer;
import fr.lirmm.aren.producer.EntityManagerProducer;
import fr.lirmm.aren.security.PasswordEncoder;

/**
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@WebFilter(asyncSupported = true, urlPatterns = { "/*" })
public class Root implements Filter {

  @Inject
  @Configurable("initialization")
  private Provider<String> initialization;

  @Inject
  @Configurable("reverse-proxy")
  private Provider<String> reverseProxy;

  @Inject
  @Configurable("smtp.server")
  private Provider<String> smtpServer;

  @Inject
  @Configurable("production")
  private Provider<Boolean> isProduction;

  @Inject
  ConfigurationProducer configurationProducer;

  @Inject
  EntityManagerProducer entityManagerProducer;

  @Inject
  private Provider<EntityManager> entityManager;

  @Inject
  private PasswordEncoder passwordEncoder;

  /**
   *
   * @param filterConfig
   * @throws ServletException
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  /**
   *
   * @param req
   * @param res
   * @param chain
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    String path = request.getRequestURI().substring(request.getContextPath().length());

    if (request.getParameter("submitInitialization") != null) {
      if (request.getParameter("server") != null) {
        this.initDatabase(request);
        request.setAttribute("setParams", true);
      } else {
        this.initParams(request);
      }
    }
    if (initialization.get().equals("true")) {
      request.getRequestDispatcher("/config.jsp").forward(request, response);
    } else {
      if (!path.startsWith("/caslogin") && !path.startsWith("/ws") && !path.startsWith("/assets")) {
        if (smtpServer.get().length() > 0)
          request.setAttribute("canSignin", true);
        if (!isProduction.get())
          request.setAttribute("development", true);

        request.setAttribute("serverRoot", reverseProxy.get());

        request.getRequestDispatcher("/index.jsp").forward(request, response);
      } else {
        chain.doFilter(request, response);
      }
    }
  }

  public void initDatabase(HttpServletRequest request) {
    String server = request.getParameter("server");
    String port = request.getParameter("port");
    String name = request.getParameter("name");
    String user = request.getParameter("user");
    String password = request.getParameter("password");

    try {
      entityManagerProducer.setCredentials(server, port, name, user, password);
    } catch (Throwable ex) {
      request.setAttribute("error", ex.getMessage());
      request.setAttribute("errorDetails", ExceptionUtils.getStackTrace(ex));
    }
  }

  public void initParams(HttpServletRequest request) {
    if (!request.getParameter("adminPassword").equals(request.getParameter("adminPasswordBis"))) {
      request.setAttribute("error", "Admin passwords don't match");
      return;
    }

    EntityManager em = entityManager.get();
    em.getTransaction().begin();

    Institution noInstitution;
    boolean newInstitution = false;
    try {
      noInstitution = em.createQuery("FROM Institution i "
          + "WHERE i.entId IS NULL", Institution.class)
          .getSingleResult();
    } catch (EntityNotFoundException enfe) {
      newInstitution = true;
      noInstitution = new Institution();
    }

    String adminUsername = request.getParameter("adminUsername");
    String hashedPassword = passwordEncoder.hashPassword(request.getParameter("adminPassword"));
    User superAdmin;
    boolean newAdmin = false;
    try {
      superAdmin = em.createQuery("FROM User u "
          + "WHERE u.username = :username", User.class)
          .setParameter("username", adminUsername)
          .getSingleResult();
      if (superAdmin.getAuthority() != Authority.SUPERADMIN) {
        request.setAttribute("error", "Username already exists in DB but is not SUPERADMIN");
        return;
      }
    } catch (EntityNotFoundException enfe) {
      newAdmin = true;
      superAdmin = new User();
      superAdmin.setFirstName("Super");
      superAdmin.setLastName("Admin");
      superAdmin.setUsername(adminUsername);
      superAdmin.setAuthority(Authority.SUPERADMIN);
    }

    if (newInstitution) {
      em.persist(noInstitution);
      em.refresh(noInstitution);
    }
    superAdmin.setPassword(hashedPassword);
    superAdmin.setActive(true);
    superAdmin.setTokenValidity(ZonedDateTime.now());
    superAdmin.setInstitution(noInstitution);
    if (newAdmin) {
      em.persist(superAdmin);
    } else {
      em.merge(superAdmin);
    }

    Configuration noInstiId = new Configuration("default.institution-id", Long.toString(noInstitution.getId()));
    em.persist(noInstiId);

    String adminMail = request.getParameter("adminMail");
    Configuration cfgMail = new Configuration("admin-mail", adminMail);
    em.persist(cfgMail);

    Configuration init = new Configuration("initialization",
        new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
    em.persist(init);

    String proxy = request.getParameter("reverseProxy");
    while (proxy.endsWith("/")) {
      proxy = proxy.substring(0, proxy.length() - 1);
    }
    Configuration configProxy = new Configuration("reverse-proxy", proxy);
    em.persist(configProxy);

    Configuration jwtIssuer = new Configuration("authentication.jwt.issuer", proxy);
    em.persist(jwtIssuer);

    Configuration jwtAudience = new Configuration("authentication.jwt.audience", proxy);
    em.persist(jwtAudience);

    String secret = RandomStringUtils.random(16, true, true);
    Configuration jwtSecret = new Configuration("authentication.jwt.secret", secret);
    em.persist(jwtSecret);

    String platformName;
    try {
      URL whatismyip = new URL("http://checkip.amazonaws.com");
      BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
      platformName = in.readLine();
    } catch (Throwable ex) {
      platformName = proxy;
    }
    Configuration platform = new Configuration("platform", platformName);
    em.persist(platform);

    em.getTransaction().commit();

    configurationProducer.loadDB();
  }

  /**
   *
   */
  @Override
  public void destroy() {
  }

}
