package org.engine.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@Profile("!dev")
//@Order(SecurityProperties.DEFAULT_FILTER_ORDER) // Use this to apply HttpSecurity security chains
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private BCryptPasswordEncoder passwordEncoder;
    private UserDetailsHandler detailsHandler;
    private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public WebSecurityConfig(BCryptPasswordEncoder passwordEncoder, UserDetailsHandler detailsHandler,
                             OAuth2AuthenticationEntryPoint authenticationEntryPoint){
        this.passwordEncoder = passwordEncoder;
        this.detailsHandler = detailsHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    // Without @Order(SecurityProperties.DEFAULT_FILTER_ORDER) it's not applied in first order
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .httpBasic()
                .and()
            // Configure token authentication permissions
            .authorizeRequests().antMatchers(HttpMethod.POST, "/oauth/token").authenticated()
                .and()
            // Configure token revoke permissions
            .authorizeRequests().antMatchers(HttpMethod.POST, "/oauth/revoke").permitAll()
            // Allow all requests by logged in users
            .anyRequest().authenticated()
                .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // If a user try to access a resource without having enough permissions
//        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/users/authorize");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(detailsHandler)
                .passwordEncoder(passwordEncoder);
    }
}
