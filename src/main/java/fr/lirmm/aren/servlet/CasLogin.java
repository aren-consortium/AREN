package fr.lirmm.aren.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import fr.lirmm.aren.model.User;
import fr.lirmm.aren.producer.Configurable;
import fr.lirmm.aren.security.token.AuthenticationTokenService;
import fr.lirmm.aren.service.CasAuthentificationService;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.Cookie;

/**
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@WebServlet(name = "CasLogin", urlPatterns = {"/caslogin"})
public class CasLogin extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1063853970050165333L;

    @Inject
    private CasAuthentificationService casService;

    @Inject
    private AuthenticationTokenService authenticationTokenService;

    @Inject
    @Configurable("reverse-proxy")
    private String proxyUrl;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (request.getParameter("ticket") != null) {
            User user = null;
            user = casService.getUserWithTicket(request.getParameter("ticket"));
            String token = authenticationTokenService.issueToken(user);

            int maxAge = 360 * 24 * 60 * 60;

            String domain;
            try {
                domain = new URL(request.getRequestURL().toString()).getHost();
                Cookie cookie = new Cookie(HttpHeaders.AUTHORIZATION, token);
                cookie.setComment("Authentification token for Aren platform");
                cookie.setDomain(domain);
                cookie.setPath("/");
                cookie.setMaxAge(maxAge);
                cookie.setSecure(false);
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            } catch (MalformedURLException ex) {
            }
            if (proxyUrl.isEmpty()) {
                response.sendRedirect("/");
            } else {
                response.sendRedirect(proxyUrl);
            }
        } else {
            response.sendRedirect(casService.getRedirectionUrl());
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
