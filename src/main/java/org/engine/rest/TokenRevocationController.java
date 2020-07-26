package org.engine.rest;

import org.engine.security.oauth.revoke.RevocationService;
import org.engine.security.oauth.revoke.RevocationServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TokenRevocationController {

    private RevocationServiceFactory revocationServiceFactory;

    @Autowired
    public TokenRevocationController(RevocationServiceFactory revocationServiceFactory){
        this.revocationServiceFactory = revocationServiceFactory;
    }

    @PostMapping("/oauth/revoke")
    public ResponseEntity<String> revoke(@RequestParam Map<String, String> params) {
        RevocationService revocationService = revocationServiceFactory
                .create(params.get("token_type_hint"));

        revocationService.revoke(params.get("token"));

        return ResponseEntity.ok().build();
    }

}
