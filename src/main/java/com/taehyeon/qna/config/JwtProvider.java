package com.taehyeon.qna.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long tokenValidityInMilliSeconds = 1000L * 60 * 30;

    /**
     * userId로 부터 JWT 토큰 생성
     * @param userId
     * @return
     */
    public String createToken(UUID userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliSeconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰으로부터 userId 추출
     * @param token
     * @return
     */
    public UUID getUserId(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return UUID.fromString(subject);
    }

    /**
     * JWT 토큰 유효성 검증
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }
}
