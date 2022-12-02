package fr.lirmm.aren.security.token;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Provider;

import fr.lirmm.aren.producer.Configurable;

/**
 * Settings for signing and verifying JWT tokens.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@Dependent
class AuthenticationTokenSettings {

    /**
     * Secret for signing and verifying the token signature.
     */
    @Inject
    @Configurable("authentication.jwt.secret")
    private Provider<String> secret;

    /**
     * Allowed clock skew for verifying the token signature (in seconds).
     */
    @Inject
    @Configurable("authentication.jwt.clockSkew")
    private Provider<Long> clockSkew;

    /**
     * Identifies the recipients that the JWT token is intended for.
     */
    @Inject
    @Configurable("authentication.jwt.audience")
    private Provider<String> audience;

    /**
     * Identifies the JWT token issuer.
     */
    @Inject
    @Configurable("authentication.jwt.issuer")
    private Provider<String> issuer;
    /**
     * JWT claim for the authorities.
     */
    private String authorityClaimName;
    /**
     * JWT claim for the token refreshment count.
     */
    private String refreshCountClaimName;
    /**
     * JWT claim for the maximum times that a token can be refreshed.
     */
    private String refreshLimitClaimName;

    AuthenticationTokenSettings() {
      this.authorityClaimName = "authorities";
      this.refreshCountClaimName = "refreshCount";
      this.refreshLimitClaimName = "refreshLimit";
    }

    public String getSecret() {
        return secret.get();
    }

    public Long getClockSkew() {
        return clockSkew.get();
    }

    public String getAudience() {
        return audience.get();
    }

    public String getIssuer() {
        return issuer.get();
    }

    public String getAuthorityClaimName() {
        return authorityClaimName;
    }

    public String getRefreshCountClaimName() {
        return refreshCountClaimName;
    }

    public String getRefreshLimitClaimName() {
        return refreshLimitClaimName;
    }
}
