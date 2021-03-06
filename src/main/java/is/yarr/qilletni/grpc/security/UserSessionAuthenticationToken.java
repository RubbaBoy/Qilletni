package is.yarr.qilletni.grpc.security;

import is.yarr.qilletni.auth.SessionHandler;
import is.yarr.qilletni.user.UserInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.UUID;

/**
 * An authentication token to authenticate users found in the standard {@link SessionHandler}.
 */
public class UserSessionAuthenticationToken extends AbstractAuthenticationToken {

    private UUID sessionId;
    private final UserInfo userInfo;

    /**
     * Creates an initial {@link UserSessionAuthenticationToken} that has yet to be authenticated.
     *
     * @param sessionId The session ID of the user
     */
    public UserSessionAuthenticationToken(UUID sessionId) {
        super(null);
        this.sessionId = sessionId;
        this.userInfo = null;
        setAuthenticated(false);
    }

    /**
     * Creates an authenticated {@link UserSessionAuthenticationToken} with the {@link UserInfo} who the
     * {@code sessionId} belongs to.
     *
     * @param sessionId The session ID of the user
     * @param userInfo The information regarding the user
     * @param authorities The authorities of the user
     */
    UserSessionAuthenticationToken(UUID sessionId, UserInfo userInfo, Collection<GrantedAuthority> authorities) {
        super(authorities);
        this.sessionId = sessionId;
        this.userInfo = userInfo;
        super.setAuthenticated(true);
    }

    /**
     * Gets the user's session ID.
     *
     * @return The session ID
     */
    @Override
    public UUID getCredentials() {
        return sessionId;
    }

    @Override
    public UserInfo getPrincipal() {
        return userInfo;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.sessionId = null;
    }
}
