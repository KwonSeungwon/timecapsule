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


/**
 * 스프링 시큐리티 컨피규레이션 (Spring Boot 3.x 대응)
 * 귀엽고 카와이한 승원이가 작성함 (AI가 최신버전으로 업그레이드 함)
 * @author 카와이승원
 * @since 2022-07 (Updated 2024)
 */
@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // CSRF 방지 비활성화
            .cors(AbstractHttpConfigurer::disable) // CORS 설정 (필요 시 나중에 구체화)
            .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 폼 미사용
            .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 미사용
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // H2 Console 등을 위해 FrameOptions 비활성화
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/**", "/h2-console/**", "/static/**", "/index.html").permitAll() // 기본 경로 허용
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
