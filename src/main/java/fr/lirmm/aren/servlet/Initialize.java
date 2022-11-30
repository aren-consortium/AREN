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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
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
@WebServlet(name = "Initialize", urlPatterns = { "/init" })
public class Initialize extends HttpServlet {

  @Inject
  ConfigurationProducer configurationProducer;

  @Inject
  EntityManagerProducer entityManagerProducer;

  @Inject
  private Provider<EntityManager> entityManager;

  @Inject
  private PasswordEncoder passwordEncoder;

  @Inject
  @Configurable("initialization")
  private Provider<String> initialization;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request  servlet request
   * @param response servlet response
   * @throws java.io.IOException
   * @throws javax.servlet.ServletException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    request.getRequestDispatcher("/config.jsp").forward(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request  servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      if (request.getParameter("setDatabase") != null) {
        String server = request.getParameter("server");
        String port = request.getParameter("port");
        String name = request.getParameter("name");
        String user = request.getParameter("user");
        String password = request.getParameter("password");
        entityManagerProducer.setCredentials(server, port, name, user, password);

        configurationProducer.loadDB();

        if (initialization.get().equals("true")) {
          request.setAttribute("setAdmin", true);
          request.getRequestDispatcher("/config.jsp").forward(request, response);
        } else {
          request.setAttribute("dbExists", true);
          request.getRequestDispatcher("/config.jsp").forward(request, response);
        }
      } else if (request.getParameter("setAdmin") != null) {
        request.setAttribute("setAdmin", true);
        if (!request.getParameter("adminPassword").equals(request.getParameter("adminPasswordBis"))) {
          throw new RuntimeException("Passwords don't match");
        }

        EntityManager em = entityManager.get();

        em.getTransaction().begin();

        Institution noInstitution = new Institution();
        em.persist(noInstitution);
        em.refresh(noInstitution);

        Configuration instiId = new Configuration("default.institution.id", Long.toString(noInstitution.getId()));
        em.persist(instiId);

        String adminMail = request.getParameter("adminMail");
        String adminUsername = request.getParameter("adminUsername");
        String hashedPassword = passwordEncoder.hashPassword(request.getParameter("adminPassword"));

        User superAdmin = new User();
        superAdmin.setFirstName("Super");
        superAdmin.setLastName("Admin");
        superAdmin.setEmail(adminMail);
        superAdmin.setUsername(adminUsername);
        superAdmin.setPassword(hashedPassword);
        superAdmin.setActive(true);
        superAdmin.setTokenValidity(ZonedDateTime.now());
        superAdmin.setAuthority(Authority.SUPERADMIN);
        superAdmin.setInstitution(noInstitution);
        em.persist(superAdmin);

        Configuration init = new Configuration("initialization",
            new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        em.persist(init);

        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        String ip = in.readLine();
        Configuration platform = new Configuration("platform.id", ip);
        em.persist(platform);

        String proxy = request.getRequestURL().toString();
        proxy = proxy.substring(0, proxy.length() - "/init".length());
        Configuration reverseProxy = new Configuration("reverse-proxy", proxy + "/");
        em.persist(reverseProxy);

        Configuration jwtIssuer = new Configuration("authentication.jwt.issuer", proxy + "/");
        em.persist(jwtIssuer);

        Configuration jwtAudience = new Configuration("authentication.jwt.audience", proxy + "/");
        em.persist(jwtAudience);

        String secret = RandomStringUtils.random(16, true, true);
        Configuration jwtSecret = new Configuration("authentication.jwt.secret", secret);
        em.persist(jwtSecret);

        em.getTransaction().commit();

        configurationProducer.loadDB();

        response.sendRedirect(request.getContextPath());
      }
    } catch (Throwable ex) {
      request.setAttribute("error", ex.getMessage());
      request.setAttribute("errorDetails", ExceptionUtils.getStackTrace(ex));
      request.getRequestDispatcher("/config.jsp").forward(request, response);
    }

  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }

}
