package com.br.apiToDoList.infra.security;


import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    
    @Autowired
    private ThrowingAuthenticationEntryPoint throwingAuthenticationEntryPoint;

    
    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
               .csrf(csrf -> csrf.disable())
               .cors(cors -> cors.configurationSource(corsConfigurationSource()))
               .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .authorizeHttpRequests(authorize -> authorize
                                    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                    .requestMatchers(HttpMethod.POST, "/auth/register" ).permitAll()
                                    .requestMatchers(
                                    "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html").permitAll()
                                    .anyRequest().authenticated()
                                    ).exceptionHandling(ex -> ex.authenticationEntryPoint(throwingAuthenticationEntryPoint))
               .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
               .build();
    }       
    
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authenticationConfiguration)  throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
