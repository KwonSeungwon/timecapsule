package com.mini.timecapsule.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;
import java.util.List;

public class TokenProvider {
    private final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

    /**
     * 토큰 만료시간 - milliseconds
     */
    private final Integer validity;
    /**
     * 인증키
     */
    private final Key key;

    /**
     * 서명 알고리즘
     */
    private final SignatureAlgorithm keyAlg = SignatureAlgorithm.HS256;


    public TokenProvider(Integer validity, Key key) {
        this.validity = validity;
        this.key = key;
    }

    public String createToken(String userPk, List<String> roles, String id) {
        Date now = new Date();
        Date validityInterval = new Date(now.getTime() + this.validity);
        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)   // 헤더 타입 지정
                .setIssuer("HealthConnect")                     // 발급자 정보
                .setIssuedAt(now)                               // 발급시간
                .setExpiration(validityInterval)                // 만료시간
                .signWith(key, keyAlg)                          // 키정보 및 해싱 알고리즘 정보
                .claim("id", id)                             // 아이디
                .compact();

        return token;
    }
    /**
     * 토큰 유효성 확인
     *
     * @param token 토큰 문자열
     * @return TOKEN_STATUS - 토큰상태
     */
    public TokenDetailInfo validateToken(String token) {
        TokenDetailInfo tokenDetailInfo = new TokenDetailInfo();
        TokenStatus tokenStatus = TokenStatus.OK;

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            try {
                tokenDetailInfo.setTokenType(TokenType.valueOf(claimsJws.getBody().get("tokenType").toString()));
                tokenDetailInfo.setId(claimsJws.getBody().get("id").toString());
                tokenDetailInfo.setName(claimsJws.getBody().get("name").toString());
            } catch (Exception e) {
                LOGGER.info("토큰 정보 누락");
                tokenStatus = TokenStatus.INVALID;
            }
        } catch (ExpiredJwtException e) {
            LOGGER.info("만료된 JWT 토큰입니다.");
            tokenStatus = TokenStatus.EXPIRED;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            LOGGER.info("잘못된 JWT 서명입니다.");
            tokenStatus = TokenStatus.ILLEGAL;
        } catch (UnsupportedJwtException e) {
            LOGGER.info("지원되지 않는 JWT 토큰입니다.");
            tokenStatus = TokenStatus.ILLEGAL;
        } catch (IllegalArgumentException e) {
            LOGGER.info("JWT 토큰이 잘못되었습니다.");
            tokenStatus = TokenStatus.ILLEGAL;
        }

        tokenDetailInfo.setToken(token);
        tokenDetailInfo.setTokenStatus(tokenStatus);

        return tokenDetailInfo;
    }
}
