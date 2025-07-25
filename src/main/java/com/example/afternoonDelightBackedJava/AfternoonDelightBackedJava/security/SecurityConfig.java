//package com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.security;
//
//import com.example.afternoonDelightBackedJava.AfternoonDelightBackedJava.dto.error.response.ErrorResponse;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.AccessDeniedHandler;
//
//import java.sql.Timestamp;
//import java.time.Instant;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .exceptionHandling(handling -> handling
//                        .authenticationEntryPoint(authenticationEntryPoint())
//                        .accessDeniedHandler(accessDeniedHandler())
//                );
//
//        return http.build();
//    }
//
//    private AuthenticationEntryPoint authenticationEntryPoint() {
//        return (request, response, authException) -> {
//            logger.warn("Authentication failed for {}: {}",
//                    request.getRequestURI(),
//                    authException.getMessage());
//
//            ErrorResponse errorResponse = new ErrorResponse(
//                    Timestamp.from(Instant.now()),
//                    HttpStatus.UNAUTHORIZED.value(),
//                    "Unauthorized",
//                    "Authentication required to access this resource",
//                    request.getRequestURI(),
//                    isProduction() ? "See application logs" : authException.getMessage()
//            );
//
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
//        };
//    }
//
//    private AccessDeniedHandler accessDeniedHandler() {
//        return (request, response, accessDeniedException) -> {
//            logger.warn("Access denied for {}: {}",
//                    request.getRequestURI(),
//                    accessDeniedException.getMessage());
//
//            ErrorResponse errorResponse = new ErrorResponse(
//                    Timestamp.from(Instant.now()),
//                    HttpStatus.FORBIDDEN.value(),
//                    "Forbidden",
//                    "Insufficient permissions",
//                    request.getRequestURI(),
//                    isProduction() ? "See application logs" : accessDeniedException.getMessage()
//            );
//
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setStatus(HttpStatus.FORBIDDEN.value());
//            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
//        };
//    }
//
//    private boolean isProduction() {
//        String env = System.getProperty("spring.profiles.active");
//        return env != null && env.contains("prod");
//    }
//}