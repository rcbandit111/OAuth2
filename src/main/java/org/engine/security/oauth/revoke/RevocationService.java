package org.engine.security.oauth.revoke;

public interface RevocationService {

    void revoke(String token);

    boolean supports(String tokenTypeHint);

}
