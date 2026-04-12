package io.joshuasalcedo.commandcenter.config.security;

import io.joshuasalcedo.commandcenter.user.UserId;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final UserId ownerId;
    private final String apiKeyId;

    ApiKeyAuthenticationToken(UserId ownerId, String apiKeyId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.ownerId = ownerId;
        this.apiKeyId = apiKeyId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return apiKeyId;
    }

    @Override
    public Object getPrincipal() {
        return ownerId;
    }
}
