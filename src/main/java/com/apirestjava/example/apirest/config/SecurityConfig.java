package com.apirestjava.example.apirest.config;

import com.apirestjava.example.apirest.security.JwtAuthenticationFilter;
import com.apirestjava.example.apirest.security.JwtService;
import com.apirestjava.example.apirest.security.RestAccessDeniedHandler;
import com.apirestjava.example.apirest.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   RestAuthenticationEntryPoint entryPoint,
                                                   RestAccessDeniedHandler deniedHandler) throws Exception {
        http
                // API sans session ni cookie : CSRF inutile, chaque requête porte son JWT
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(entryPoint)   // 401
                        .accessDeniedHandler(deniedHandler))    // 403
                .authorizeHttpRequests(auth -> auth
                        // inscription, connexion, refresh, logout
                        .requestMatchers("/api/auth/**").permitAll()
                        // Exercice 3 : JWT obligatoire
                        .requestMatchers("/api/bmi/**").authenticated()
                        // Exercices 1 et 2 : inchangés
                        .anyRequest().permitAll())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}