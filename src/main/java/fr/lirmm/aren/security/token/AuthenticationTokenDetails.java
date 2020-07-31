package fr.lirmm.aren.security.token;

import java.time.ZonedDateTime;

import fr.lirmm.aren.model.User.Authority;

/**
 * Model that holds details about an authentication token.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
public final class AuthenticationTokenDetails {

    private final String id;
    private final String username;
    private final Authority authority;
    private final ZonedDateTime issuedDate;
    private final ZonedDateTime expirationDate;
    private final int refreshCount;
    private final int refreshLimit;

    private AuthenticationTokenDetails(String id, String username, Authority authority, ZonedDateTime issuedDate, ZonedDateTime expirationDate, int refreshCount, int refreshLimit) {
        this.id = id;
        this.username = username;
        this.authority = authority;
        this.issuedDate = issuedDate;
        this.expirationDate = expirationDate;
        this.refreshCount = refreshCount;
        this.refreshLimit = refreshLimit;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return
     */
    public Authority getAuthority() {
        return authority;
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getIssuedDate() {
        return issuedDate;
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getExpirationDate() {
        return expirationDate;
    }

    /**
     *
     * @return
     */
    public int getRefreshCount() {
        return refreshCount;
    }

    /**
     *
     * @return
     */
    public int getRefreshLimit() {
        return refreshLimit;
    }

    /**
     * Check if the authentication token is eligible for refreshment.
     *
     * @return
     */
    public boolean isEligibleForRefreshment() {
        return refreshCount < refreshLimit;
    }

    /**
     * Builder for the {@link AuthenticationTokenDetails}.
     */
    public static class Builder {

        private String id;
        private String username;
        private Authority authority;
        private ZonedDateTime issuedDate;
        private ZonedDateTime expirationDate;
        private int refreshCount;
        private int refreshLimit;

        /**
         *
         * @param id
         * @return
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         *
         * @param username
         * @return
         */
        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        /**
         *
         * @param authority
         * @return
         */
        public Builder withAuthority(Authority authority) {
            this.authority = authority;
            return this;
        }

        /**
         *
         * @param issuedDate
         * @return
         */
        public Builder withIssuedDate(ZonedDateTime issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        /**
         *
         * @param expirationDate
         * @return
         */
        public Builder withExpirationDate(ZonedDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        /**
         *
         * @param refreshCount
         * @return
         */
        public Builder withRefreshCount(int refreshCount) {
            this.refreshCount = refreshCount;
            return this;
        }

        /**
         *
         * @param refreshLimit
         * @return
         */
        public Builder withRefreshLimit(int refreshLimit) {
            this.refreshLimit = refreshLimit;
            return this;
        }

        /**
         *
         * @return
         */
        public AuthenticationTokenDetails build() {
            return new AuthenticationTokenDetails(id, username, authority, issuedDate, expirationDate, refreshCount, refreshLimit);
        }
    }
}
