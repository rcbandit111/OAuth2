package org.engine.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfigJwt extends AuthorizationServerConfigurerAdapter {

    @Value("${security.oauth2.resource.jwt.key-pair.store-password}")
    private String keyStorePass;

    @Value("${security.oauth2.resource.jwt.key-pair.alias}")
    private String keyPairAlias;

    private PasswordEncoder oauthClientPasswordEncoder;
    private DataSource dataSource;
    private UserClientDetailsService userClientDetailsService;
    private UserDetailsHandler detailsHandler;
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    public OAuth2AuthorizationServerConfigJwt(@Lazy PasswordEncoder oauthClientPasswordEncoder,
                                              DataSource dataSource, UserClientDetailsService userClientDetailsService,
                                              UserDetailsHandler detailsHandler,
                                              @Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager){
        this.oauthClientPasswordEncoder = oauthClientPasswordEncoder;
        this.dataSource = dataSource;
        this.userClientDetailsService = userClientDetailsService;
        this.detailsHandler = detailsHandler;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(oauthClientPasswordEncoder);
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(userClientDetailsService);//.jdbc(dataSource);

//                .inMemory()
//                .withClient("test")
//                .authorizedGrantTypes("password", "refresh_token")
//                .authorities("USER")
//                .scopes("read", "write")
//                .resourceIds("rest_service")
//                .secret("{noop}test")
//                .accessTokenValiditySeconds(500)
//                .refreshTokenValiditySeconds(600);
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setRefreshTokenValiditySeconds(300);
        return defaultTokenServices;
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager)
                .userDetailsService(detailsHandler);
    }

    @Bean
    public TokenStore tokenStore() {
//        return new JdbcTokenStore(dataSource);
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("keystore.jks"), keyStorePass.toCharArray()).getKeyPair(keyPairAlias);
        converter.setKeyPair(keyPair);
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
