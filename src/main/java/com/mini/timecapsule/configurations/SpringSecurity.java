package com.mini.timecapsule.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests(auth -> auth
                // SPA 셸과 정적 자원 — 비회원 서비스라 인증 대상이 아니다.
                // vue-router 가 history mode 라 아래 라우트들이 서버로 그대로 들어온다.
                .requestMatchers(
                        "/", "/index.html", "/favicon.ico",
                        "/write", "/find", "/my-capsule", "/tutorial", "/explore",
                        "/static/**", "/css/**", "/js/**", "/img/**", "/images/**", "/fonts/**"
                ).permitAll()
                .requestMatchers("/api/v1/**", "/api/timecapsule/**", "/h2-console/**").permitAll()
                // k8s probe 용 — health/info 만 연다. env/beans 등은 계속 막는다.
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
