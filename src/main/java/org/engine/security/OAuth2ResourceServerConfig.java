package org.engine.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.DelegatingJwtClaimsSetVerifier;
import org.springframework.security.oauth2.provider.token.store.IssuerClaimVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private DefaultTokenServices tokenServices;

    @Autowired
    public OAuth2ResourceServerConfig(DefaultTokenServices tokenServices){
        this.tokenServices = tokenServices;
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //IF_REQUIRED)
                    .and()
                // application security
                .authorizeRequests().anyRequest().authenticated()
                    .and()
                .requestMatchers().antMatchers("/users/**")
                    .and()
                .csrf().disable();
    }

    @Override
    public void configure(final ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices);
//        .resourceId("admin");
    }

    // JWT

    @Bean
    public JwtClaimsSetVerifier jwtClaimsSetVerifier() {
        return new DelegatingJwtClaimsSetVerifier(Arrays.asList(issuerClaimVerifier(), customJwtClaimVerifier()));
    }

    @Bean
    public JwtClaimsSetVerifier issuerClaimVerifier() {
        try {
            return new IssuerClaimVerifier(new URL("http://localhost:8081"));
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public JwtClaimsSetVerifier customJwtClaimVerifier() {
        return new CustomClaimVerifier();
    }
}
