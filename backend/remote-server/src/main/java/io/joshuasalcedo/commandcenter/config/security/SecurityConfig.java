package io.joshuasalcedo.commandcenter.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final Environment environment;
    private final String frontendUrl;

    SecurityConfig(OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                   ApiKeyAuthenticationFilter apiKeyAuthenticationFilter,
                   Environment environment,
                   @Value("${app.frontend-url:http://localhost:3000}") String frontendUrl) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
        this.environment = environment;
        this.frontendUrl = frontendUrl;
    }

    private boolean isProd() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    /**
     * Public API — no auth required.
     * Desktop/local updates, cicd-sample docs.
     */
    @Bean
    @Order(1)
    SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/public/**", "/health")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Admin API — API key + ROLE_ADMIN.
     * In prod, actuator endpoints also require ADMIN.
     */
    @Bean
    @Order(2)
    SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        if (isProd()) {
            http.securityMatcher("/api/admin/**", "/actuator/**");
        } else {
            http.securityMatcher("/api/admin/**");
        }

        http
            .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ADMIN"))
            .addFilterBefore(apiKeyAuthenticationFilter, OAuth2LoginAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * Authenticated API — API key or OAuth2 session.
     * Everything else under /api/** that isn't public or admin.
     */
    @Bean
    @Order(3)
    SecurityFilterChain authenticatedFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**", "/oauth2/**", "/login/oauth2/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/status").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(apiKeyAuthenticationFilter, OAuth2LoginAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .csrf(csrf -> csrf.disable())
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2LoginSuccessHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/api/logout")
                .logoutSuccessUrl(frontendUrl + "/login")
                .permitAll()
            );

        return http.build();
    }

    /**
     * Non-API paths: swagger, actuator (dev/test), error.
     */
    @Bean
    @Order(4)
    SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/error").permitAll();

                if (isProd()) {
                    auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").authenticated();
                } else {
                    auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
                    auth.requestMatchers("/actuator/**").permitAll();
                }

                auth.anyRequest().denyAll();
            })
            .addFilterBefore(apiKeyAuthenticationFilter, OAuth2LoginAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
