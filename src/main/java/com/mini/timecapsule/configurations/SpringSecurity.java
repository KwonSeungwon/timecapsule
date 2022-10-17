package com.mini.timecapsule.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * 스프링 시큐리티 컨피규레이션(옵션설쩡)
 * 귀엽고 카와이한 승원이가 작성함
 * @author 카와이승원
 * @since 2022-07
 */
@Configuration
@EnableWebSecurity
public class SpringSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().disable() // cors(교차출처리소스공유 미사용 / vue 사용중이라 이걸 하는게 맞는지모르겟음)
            .csrf().disable() // csrf 방지
            .formLogin().disable() // 시뀨리띠 기본 로긴화면 안씀
            .headers().frameOptions().disable(); // 클릭재킹을 막기위한건데 우린 그딴공격받을일 없음
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
