package org.ms.authservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/test/all").permitAll()
                    .requestMatchers("/api/test/user").hasAnyAuthority("product:read", "invoice:read")
                    .requestMatchers("/api/test/admin").hasRole("ADMIN")
                    .requestMatchers("/actuator/**").permitAll() // For service discovery
                    // Product endpoints
                    .requestMatchers(HttpMethod.GET, "/api/products/**").hasAuthority("product:read")
                    .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("product:create")
                    .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("product:update")
                    .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("product:delete")
                    // Invoice endpoints
                    .requestMatchers(HttpMethod.GET, "/api/invoices/**").hasAuthority("invoice:read")
                    .requestMatchers(HttpMethod.POST, "/api/invoices/**").hasAuthority("invoice:create")
                    .requestMatchers(HttpMethod.PUT, "/api/invoices/**").hasAuthority("invoice:update")
                    .requestMatchers(HttpMethod.DELETE, "/api/invoices/**").hasAuthority("invoice:delete")
                    .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
