package com.mini.timecapsule.configurations;

import com.mini.timecapsule.config.SpaForwardingConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.List;


/**
 * 비회원 서비스의 인가 규칙.
 *
 * <p><b>기본값이 "허용"이다.</b> 예전엔 반대였다 — SPA 라우트를 손으로 whitelist 하고
 * {@code anyRequest().authenticated()} 로 나머지를 막았다. 로그인 수단이 아예 없는
 * 서비스에서 "authenticated" 는 곧 "영원히 403" 이고, 실제로 이런 일이 났다:
 * <ul>
 *   <li>vue-router 의 {@code catchAll}(404 화면)이 하드 로드에서 죽었다.</li>
 *   <li>오타 URL 이 앱 대신 <b>빈 403</b> 을 줬다.</li>
 *   <li>새 Vue 라우트를 추가할 때마다 <b>이 Java 파일</b>을 같이 고쳐야 했고,
 *       잊으면 운영에서 새로고침할 때만 깨졌다.</li>
 * </ul>
 *
 * <p>그래서 "SPA 가 아닌 것"만 명시하고 나머지는 연다. 그 목록의 단일 출처는
 * {@link SpaForwardingConfig#NON_SPA_PREFIXES} 이고, 여기서는 그중 공개해야 하는
 * 정적 자원({@link #PUBLIC_ASSET_PREFIXES})만 빼고 <b>전부 잠근다</b>.
 *
 * <p>이 파생 방식이 중요한 이유: 나중에 누가 {@code NON_SPA_PREFIXES} 에 새 prefix 를
 * 추가하면 그건 <b>자동으로 잠금</b> 쪽에 떨어진다. 분류를 잊었을 때 실수의 방향이
 * "조용히 공개"가 아니라 "눈에 띄게 403"이다 — fail-open 이 아니라 fail-safe.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurity {

    /**
     * {@link SpaForwardingConfig#NON_SPA_PREFIXES} 중 <b>누구나 받아가야 하는</b> 정적 자원.
     *
     * <p>SPA 셸로 fallback 하지는 않지만(없으면 404 가 맞다) 공개는 되어야 한다.
     * 여기 없는 non-SPA prefix 는 전부 잠긴다.
     * {@code SpaRoutingTest} 가 이 목록이 원본과 갈라지지 않는지 검증한다.
     */
    public static final List<String> PUBLIC_ASSET_PREFIXES =
            List.of("js/", "css/", "img/", "images/", "fonts/");

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
                // 1) 공개 API — 비회원 서비스의 본체.
                .requestMatchers("/api/v1/**").permitAll()
                // 2) k8s probe 용 — health/info 만 연다. env/beans 등은 아래에서 막힌다.
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                // 3) 공개 정적 자원.
                .requestMatchers(matchersFor(PUBLIC_ASSET_PREFIXES)).permitAll()
                // 4) 나머지 non-SPA 경로(/api/**, /actuator/**, /h2-console/**)는 잠근다.
                //
                //    ⚠️ 여기에 /api/timecapsule/** 나 /h2-console/** 를 예외로 다시 열지 말 것.
                //    예전엔 둘 다 permitAll 이었고, 그 아래 살아 있던
                //    POST /api/timecapsule/login 이 비밀번호 검증 없이 서명된 JWT 를
                //    아무에게나 발급했다. NoMemberEndpointsTest 가 이 배선을 지킨다.
                //    H2 콘솔 서블릿이 지금 꺼져 있어도 규칙은 남겨둔다 — 누가
                //    spring.h2.console.enabled 를 켜는 순간 DB 가 통째로 공개되면 안 된다.
                .requestMatchers(lockedDownNonSpaMatchers()).denyAll()
                // 5) 나머지는 전부 SPA 라우트다. 새 Vue 라우트를 추가할 때
                //    이 파일을 고칠 필요가 없어야 한다.
                .anyRequest().permitAll()
            );

        return http.build();
    }

    /**
     * {@code NON_SPA_PREFIXES} 에서 공개 정적 자원을 뺀 나머지 → Ant 패턴.
     * 예: {@code "api/"} → {@code "/api/**"}.
     */
    private static String[] lockedDownNonSpaMatchers() {
        return Arrays.stream(SpaForwardingConfig.NON_SPA_PREFIXES)
                .filter(prefix -> !PUBLIC_ASSET_PREFIXES.contains(prefix))
                .map(SpringSecurity::toAntPattern)
                .toArray(String[]::new);
    }

    private static String[] matchersFor(List<String> prefixes) {
        return prefixes.stream().map(SpringSecurity::toAntPattern).toArray(String[]::new);
    }

    private static String toAntPattern(String prefix) {
        return "/" + prefix + "**";
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
