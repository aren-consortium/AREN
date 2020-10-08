package fr.lirmm.aren.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;

/**
 * Service that provides operations for CAS authentification
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class CasAuthentificationService {

    @Inject
    private UserService userService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    @Configurable("cas.url")
    private String serverUrl;

    @Inject
    @Configurable("reverse-proxy")
    private String proxyUrl;

    @Inject
    private HttpServletRequest request;

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

    private String getClientUrl() {
        String clientUrl;
        if (proxyUrl.length() == 0) {
            clientUrl = request.getRequestURL().toString();
        } else {
            clientUrl = proxyUrl + "/caslogin";
        }
        return clientUrl;
    }

    /**
     *
     * @return
     */
    public String getRedirectionUrl() {
        return (serverUrl + "/login?service=" + encodeUrl(getClientUrl()));
    }

    /**
     *
     * @param ticket
     * @return
     * @throws InvalidAuthenticationTokenException
     */
    public User getUserWithTicket(String ticket) throws InvalidAuthenticationTokenException {
        URL casXml = genURL(serverUrl + "/p3/serviceValidate?service=" + encodeUrl(getClientUrl()) + "&ticket=" + ticket);
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
