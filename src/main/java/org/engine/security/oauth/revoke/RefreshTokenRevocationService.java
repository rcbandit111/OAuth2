package org.engine.security.oauth.revoke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenRevocationService implements RevocationService {

    private TokenStore tokenStore;

    @Autowired
    public RefreshTokenRevocationService(TokenStore tokenStore){
        this.tokenStore = tokenStore;
    }

    @Override
    public void revoke(String token) {
        if (tokenStore instanceof JdbcTokenStore) {
            JdbcTokenStore store = (JdbcTokenStore) tokenStore;
            store.removeRefreshToken(token);
        }
    }

    @Override
    public boolean supports(String tokenTypeHint) {
        return "refresh_token".equals(tokenTypeHint);
    }

}
