package fr.lirmm.aren.service;

import fr.lirmm.aren.producer.Configurable;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.AddressException;

/**
 *
 * @author florent
 */
@ApplicationScoped
public class MailingService {

    @Inject
    @Configurable("smtp.server")
    private String smtpServer;

    @Inject
    @Configurable("smtp.username")
    private String smtpUsername;

    @Inject
    @Configurable("smtp.password")
    private String smtpPassword;

    @Inject
    @Configurable("smtp.port")
    private String smtpPort;

    @Inject
    @Configurable("smtp.tls")
    private String smtpTls;

    @Inject
    @Configurable("smtp.auth")
    private String smtpAuth;

    /**
     *
     * @param from
     * @param to
     * @param subject
     * @param content
     */
    public void sendMail(String from, String to, String subject, String content) throws AddressException, MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", smtpTls);
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", smtpPort);

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });

        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);

        // Set From: header field of the header.
        message.setFrom(new InternetAddress(from));

        // Set To: header field of the header.
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));

        // Set Subject: header field
        message.setSubject(subject);

        // Send the actual HTML message, as big as you like
        message.setContent(content, "text/html");

        // Send message
        Transport.send(message);
    }

}
