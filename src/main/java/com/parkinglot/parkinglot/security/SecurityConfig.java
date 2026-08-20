package com.parkinglot.parkinglot.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"error\":\"Authentication required\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"error\":\"You do not have permission to access this resource\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/users").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/parking-lots").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/slots").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/venues/my", "/venues/my/*", "/venues/*/pricing").hasRole("VENDOR")
                        .requestMatchers(HttpMethod.POST, "/venues", "/venues/*/publish", "/venues/*/unpublish", "/venues/*/pricing", "/venues/*/parking-areas", "/parking-areas/*/slots").hasRole("VENDOR")
                        .requestMatchers(HttpMethod.PUT, "/venues/**", "/parking-areas/**", "/slots/*").hasRole("VENDOR")
                        .requestMatchers(HttpMethod.DELETE, "/venues/**", "/parking-areas/**", "/slots/*").hasRole("VENDOR")
                        .requestMatchers(HttpMethod.POST, "/reservations").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/reservations", "/reservations/my", "/reservations/*").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/reservations/*").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/payments").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/payments/my", "/payments/*").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/venues", "/venues/*", "/venues/*/parking-areas", "/parking-areas/*/slots", "/parking-areas/*/slots/available", "/parking-lots", "/parking-lots/*", "/slots", "/slots/available").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
