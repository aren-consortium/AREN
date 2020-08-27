package fr.lirmm.aren.security.token;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fr.lirmm.aren.producer.Configurable;
import fr.lirmm.aren.model.User;
import java.time.temporal.ChronoUnit;

/**
 * Service which provides operations for authentication tokens.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@ApplicationScoped
public class AuthenticationTokenService {

    /**
     * How long the token is valid for (in seconds).
     */
    @Inject
    @Configurable("authentication.jwt.validFor")
    private Long validFor;

    @Inject
    private AuthenticationTokenIssuer tokenIssuer;

    @Inject
    private AuthenticationTokenParser tokenParser;

    /**
     * Issue a token for a user with the given authorities.
     *
     * @param user
     * @param validFor
     * @return
     */
    public String issueToken(User user, Long validFor) {

        String id = generateTokenIdentifier();
        ZonedDateTime issuedDate = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        AuthenticationTokenDetails authenticationTokenDetails = new AuthenticationTokenDetails.Builder()
                .withId(id)
                .withUsername(user.getUsername())
                .withAuthority(user.getAuthority())
                .withIssuedDate(issuedDate)
                .withExpirationDate(issuedDate.plusSeconds(validFor))
                .withRefreshCount(0)
                .build();

        return tokenIssuer.issueToken(authenticationTokenDetails);
    }

    /**
     * Issue a token for a user with the given authorities.
     *
     * @param user
     * @return
     */
    public String issueToken(User user) {

        return issueToken(user, validFor);
    }

    /**
     * Parse and validate the token.
     *
     * @param token
     * @return
     */
    public AuthenticationTokenDetails parseToken(String token) {
        return tokenParser.parseToken(token);
    }

    /**
     * Generate a token identifier.
     *
     * @return
     */
    private String generateTokenIdentifier() {
        return UUID.randomUUID().toString();
    }
}
