package com.mini.timecapsule.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
            // frameOptions 는 H2 콘솔을 iframe 으로 띄우려고 꺼져 있었다. 콘솔을 닫았으니
            // 기본값(DENY)으로 되돌린다 — 캡슐 내용이 뜨는 화면이 남의 페이지에
            // iframe 으로 실릴 이유가 없다(클릭재킹).
            .authorizeHttpRequests(auth -> auth
                // SPA 셸과 정적 자원 — 비회원 서비스라 인증 대상이 아니다.
                // vue-router 가 history mode 라 아래 라우트들이 서버로 그대로 들어온다.
                .requestMatchers(
                        "/", "/index.html", "/favicon.ico",
                        "/write", "/find", "/my-capsule", "/tutorial", "/explore",
                        "/static/**", "/css/**", "/js/**", "/img/**", "/images/**", "/fonts/**"
                ).permitAll()
                // ⚠️ 여기에 /api/timecapsule/** 나 /h2-console/** 를 다시 넣지 말 것.
                //    이 서비스는 비회원 전용이라 캡슐 API(/api/v1/**) 말고는 열 게 없다.
                //    예전엔 둘 다 permitAll 이었고, 그 아래 살아 있던
                //    POST /api/timecapsule/login 이 비밀번호 검증 없이 서명된 JWT 를
                //    아무에게나 발급했다. NoMemberEndpointsTest 가 이 배선을 지킨다.
                .requestMatchers("/api/v1/**").permitAll()
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
