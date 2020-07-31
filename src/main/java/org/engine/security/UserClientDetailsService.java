package org.engine.security;

import org.engine.production.entity.Users;
import org.engine.production.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserClientDetailsService implements ClientDetailsService {

    private UsersService usersService;

    @Autowired
    public UserClientDetailsService(UsersService usersService){
        this.usersService = usersService;
    }

    @Override
    public ClientDetails loadClientByClientId(String username) {

        final Optional<Users> user = usersService.findByLogin(username);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }

        return user
            .map(value -> {

                BaseClientDetails details = new BaseClientDetails();
                details.setClientId(value.getLogin());
                details.setClientSecret(value.getEncryptedPassword());
                details.setAuthorizedGrantTypes(Arrays.asList("password", "refresh_token"));
                details.setScope(Arrays.asList("read", "write", "offline_access"));
                details.setRegisteredRedirectUri(Collections.singleton("http://anywhere.com"));
                details.setResourceIds(Arrays.asList("oauth2-resource"));
                Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
                authorities.add(new SimpleGrantedAuthority(value.getRole().getAuthority()));
                details.setAuthorities(authorities);
                details.setRefreshTokenValiditySeconds(300);
                details.setAccessTokenValiditySeconds(100);

                return details;
            }).orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
    }
}
