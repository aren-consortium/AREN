package fr.lirmm.aren.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.lirmm.aren.producer.Configurable;

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

    if (initialization.get().equals("true")) {
      if (path.startsWith("/init")) {
        chain.doFilter(request, response);
      } else {
        response.sendRedirect(request.getContextPath() + "/init");
      }
    } else {
      if (!path.startsWith("/caslogin") && !path.startsWith("/ws") && !path.startsWith("/assets")) {
        if (smtpServer.get().length() > 0)
          request.setAttribute("canSingin", true);
        if (!isProduction.get())
          request.setAttribute("development", true);

        request.setAttribute("serverRoot", reverseProxy.get());

        request.getRequestDispatcher("/index.jsp").forward(request, response);
      } else {
        chain.doFilter(request, response);
      }
    }
  }

  /**
   *
   */
  @Override
  public void destroy() {
  }

}
