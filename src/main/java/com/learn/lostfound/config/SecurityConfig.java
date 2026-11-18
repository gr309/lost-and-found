
package com.learn.lostfound.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)

            // Enable CORS
            .cors(Customizer.withDefaults())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // Public endpoints
                .requestMatchers(
                    "/",
                    "/health/",
                    "/api/auth/signup/",
                    "/api/auth/signin/"
                ).permitAll()

                // Anyone can browse items
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/items/",
                    "/api/items/**",
                    "/api/me/", 
                    "/api/claims/",
                    "/api/claims/**", 
                    "/api/notifications/", 
                    "/api/notifications/**" 
                ).permitAll()

                // Other endpoints require authentication
                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            );

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") String allowedOrigins) {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(
            Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .toList()
        );

        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
            "Authorization", "Content-Type", "Accept"
        ));

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

// package com.learn.lostfound.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import jakarta.servlet.DispatcherType;
// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//         http
//             .csrf(AbstractHttpConfigurer::disable)
//             .sessionManagement(session ->
//                 session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//             )
//             .authorizeHttpRequests(auth -> auth

//                 // Public endpoints
//                 .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
//                 .requestMatchers(
//                     "/",
//                     "/health/",
//                     "/api/auth/signup/",
//                     "/api/auth/signin/"
//                 ).permitAll()

//                 // Public GET endpoints
//                 .requestMatchers(
//                     HttpMethod.GET,
//                     "/api/items/",
//                     "/api/items/**"
//                 ).permitAll()

//                 // Other endpoints require authentication
//                 .anyRequest().authenticated()
//             )
//             .oauth2ResourceServer(oauth2 ->
//                 oauth2.jwt(Customizer.withDefaults())
//             );

//         return http.build();
//     }
// }