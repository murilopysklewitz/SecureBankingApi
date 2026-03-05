package com.SecureBankingApi.infrastructure.security;


import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .csrf(csfc -> csfc.disable())

                .cors(cors -> cors.configure(http))

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/swagger-ui.html").permitAll()
                                .requestMatchers("/api-docs/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                                .requestMatchers(HttpMethod.GET, "/actuator/info").permitAll()
                                .requestMatchers("/actuator/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()


                                .requestMatchers(HttpMethod.GET, "/api/accounts/").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/accounts/{id}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/accounts/{id}/balance").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/accounts/{id}/block").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/accounts/{id}/unblock").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/accounts/{id}").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.POST, "/api/transactions/transfer").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/transactions/deposit").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/transactions/withdraw").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/transactions/accounts/{accountId}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/transactions/reverse/{transactionId}").hasRole("ADMIN")
                        .anyRequest().authenticated()
                        );


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();


        config.setAllowedOrigins(List.of("http://3.141.250.199:3000", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
