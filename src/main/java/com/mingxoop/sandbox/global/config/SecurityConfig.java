package com.mingxoop.sandbox.global.config;

import com.mingxoop.sandbox.global.api.AppCookie;
import com.mingxoop.sandbox.global.jwt.JwtAuthenticationFilter;
import com.mingxoop.sandbox.global.jwt.JwtRepository;
import com.mingxoop.sandbox.global.jwt.JwtVerificationFilter;
import com.mingxoop.sandbox.global.properties.CorsProperties;
import com.mingxoop.sandbox.global.security.AppAccessDeniedHandler;
import com.mingxoop.sandbox.global.security.AppAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final CorsProperties corsProperties;
    private final JwtRepository jwtRepository;
    private final AppCookie appCookie;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AppAuthenticationEntryPoint authenticationEntryPoint, AppAccessDeniedHandler accessDeniedHandler) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 X
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint) // 인증 실패 처리
                        .accessDeniedHandler(accessDeniedHandler) // 권한 부족(403) 처리
                )
                .addFilterBefore(
                        jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(
                        jwtVerificationFilter(),
                        JwtAuthenticationFilter.class
                )
                .build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowMethods());
        configuration.setAllowCredentials(corsProperties.isAllowCredentials());
        for (String exposedHeader : corsProperties.getExposedHeaders()) {
            configuration.addExposedHeader(exposedHeader);
        }
        for (String allowedHeader : corsProperties.getAllowedHeaders()) {
            configuration.addAllowedHeader(allowedHeader);
        }
        configuration.setMaxAge(corsProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                authenticationManager(authenticationConfiguration),
                appCookie,
                jwtRepository
        );

        filter.setFilterProcessesUrl("/api/auth/login");

        return filter;
    }

    public JwtVerificationFilter jwtVerificationFilter() throws Exception {

        return new JwtVerificationFilter(
                jwtRepository
        );
    }
}