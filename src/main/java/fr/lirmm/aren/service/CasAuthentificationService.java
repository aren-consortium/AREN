package fr.lirmm.aren.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.lirmm.aren.producer.Configurable;
import fr.lirmm.aren.exception.InvalidAuthenticationTokenException;
import fr.lirmm.aren.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 * Service that provides operations for CAS authentification
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Dependent
public class CasAuthentificationService {

    @Inject
    private UserService userService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    @Configurable("cas.url")
    private String serverUrl;

    private String loginUrl;

    private String validationUrl;

    @Context
    private HttpServletRequest request;

    /**
     *
     */
    public CasAuthentificationService() {
        loginUrl = serverUrl + "/login";
        validationUrl = serverUrl + "/serviceValidate";
        // restUrl = loginUrl + "/ct_logon_mixte.jsp"
    }

    private String encodeUrl(String url) {
        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CasAuthentificationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encodedUrl;
    }

    private URL genURL(String url) {
        URL result = null;
        try {
            result = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CasAuthentificationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     *
     * @return
     */
    public String getRedirectionUrl() {
        String clientUrl = request.getRequestURI();
        return (loginUrl + "?service=" + encodeUrl(clientUrl + "/caslogin"));
    }

    /**
     *
     * @param ticket
     * @return
     * @throws InvalidAuthenticationTokenException
     */
    public User getUserWithTicket(String ticket) throws InvalidAuthenticationTokenException {
        String clientUrl = request.getRequestURI();
        URL casXml = genURL(validationUrl + "?service=" + encodeUrl(clientUrl + "/caslogin") + "&ticket=" + ticket);
        Document doc = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(casXml.openStream());
            doc.getDocumentElement().normalize();
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            throw InvalidAuthenticationTokenException.INVALID_CAS_TICKET();
        }

        NodeList success = doc.getElementsByTagName("cas:authenticationSuccess");

        if (success.getLength() == 0) {
            NodeList failure = doc.getElementsByTagName("cas:authenticationFailure");
            if (failure.getLength() > 0) {
                String code = failure.item(0).getAttributes().getNamedItem("code").getTextContent();
                throw InvalidAuthenticationTokenException.INVALID_CAS_TICKET(code);
            }
            throw InvalidAuthenticationTokenException.INVALID_CAS_TICKET();
        } else {
            String username = doc.getElementsByTagName("cas:user").item(0).getTextContent();
            User user = userService.findByUsernameOrEmail(username);
            if (user == null) {
                user = new User();
                user.setUsername(username);
                user.setAuthority(User.Authority.USER);
                user.setInstitution(institutionService.getReference(0L));
                userService.create(user);
            }
            return user;
        }
    }
}
