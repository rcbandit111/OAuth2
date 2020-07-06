package org.engine.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize and @PostAuthorize annotations
@Profile("!dev")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private AuthenticationExceptionHandlerEntryPoint handler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // HttpSecurity is the first layer
        // @formatter:off
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"/users/authorize").permitAll()
                .antMatchers(HttpMethod.POST,"/users/reset_request").permitAll()
                .antMatchers(HttpMethod.POST,"/users/reset_token").permitAll()
                .antMatchers(HttpMethod.POST,"/users/reset_password").permitAll()
                .antMatchers(HttpMethod.POST,"/users/confirmation_token").permitAll()
                .antMatchers(HttpMethod.POST,"/users/reset_user_password").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().permitAll()
                .and().csrf().disable();

        http.httpBasic().disable();

        // If a user try to access a resource without having enough permissions
        http.exceptionHandling().authenticationEntryPoint(handler);

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // @formatter:on
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        // WebSecurity is the second layer
        web.ignoring()
                .antMatchers(HttpMethod.POST,"/users/authorize")
                .antMatchers(HttpMethod.POST,"/users/reset_request")
                .antMatchers(HttpMethod.POST,"/users/reset_token")
                .antMatchers(HttpMethod.POST,"/users/reset_password")
                .antMatchers(HttpMethod.POST,"/users/confirmation_token")
                .antMatchers(HttpMethod.POST,"/users/reset_user_password");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder(12);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }
}
