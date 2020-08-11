package org.engine.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

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
    public OAuth2AuthorizationServerConfigJwt(PasswordEncoder oauthClientPasswordEncoder,
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
//                .sslOnly();
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.withClientDetails(userClientDetailsService).build();//.jdbc(dataSource);

        clients.inMemory()
                .withClient("web")
                .secret(oauthClientPasswordEncoder.encode("secret"))
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("all")
                .autoApprove(true);

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
        // These are default settings. See UserClientDetailsService for actual applied timeout settings for user
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setAccessTokenValiditySeconds(60);
        defaultTokenServices.setRefreshTokenValiditySeconds(80);
        defaultTokenServices.setReuseRefreshToken(false);
        return defaultTokenServices;
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
        endpoints.approvalStore(approvalStore());
        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager)
                .userDetailsService(detailsHandler)
                // With this option set to false we need to use every time new refresh token when the token is renewed
                .reuseRefreshTokens(false);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
//        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
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
}
