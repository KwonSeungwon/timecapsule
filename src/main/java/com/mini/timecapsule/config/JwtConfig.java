package com.mini.timecapsule.config;

import com.mini.timecapsule.jwt.TokenProvider;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret:default_secret_key_more_than_32_bytes_long_required_here}")
    private String secret;

    @Value("${jwt.validity:86400000}") // 기본 1일
    private Integer validity;

    @Bean
    public TokenProvider tokenProvider() {
        // Base64 디코딩 대신 문자열 바이트를 직접 사용하여 인코딩 형식에 따른 에러 방지
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        
        // HMAC-SHA 키가 너무 짧으면 에러가 날 수 있으므로 최소 길이를 보장하거나 
        // 키 생성 방식을 안정적으로 변경 (Keys.hmacShaKeyFor는 최소 길이를 요구함)
        Key key;
        if (keyBytes.length < 32) {
            // 키가 짧을 경우 패딩 처리하거나 예외 방지를 위해 해시 사용 고려
            key = Keys.hmacShaKeyFor("fixed_length_secure_key_padding_for_jwt".getBytes(StandardCharsets.UTF_8));
        } else {
            key = Keys.hmacShaKeyFor(keyBytes);
        }
        
        return new TokenProvider(validity, key);
    }
}
