package org.engine.security.oauth.revoke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenRevocationService implements RevocationService {

    private ConsumerTokenServices tokenService;

    @Autowired
    public AccessTokenRevocationService(ConsumerTokenServices tokenService){
        this.tokenService = tokenService;
    }

    @Override
    public void revoke(String token) {
        tokenService.revokeToken(token);
    }

    @Override
    public boolean supports(String tokenTypeHint) {
        return "access_token".equals(tokenTypeHint);
    }
}
