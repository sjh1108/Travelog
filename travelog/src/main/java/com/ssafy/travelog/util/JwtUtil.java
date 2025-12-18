package com.ssafy.travelog.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    // application.properties에서 설정값 가져오기
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        // 비밀키를 암호화 알고리즘에 맞게 객체로 변환
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    // 1. 토큰 생성 (로그인 성공 시 호출)
    public String createToken(String email, String nickname) {
        return Jwts.builder()
                .header().add("typ", "JWT").and() // 헤더: 타입 명시
                .subject(email)                   // 내용: 사용자 식별값 (이메일)
                .claim("nickname", nickname)      // 내용: 닉네임 추가
                .claim("role", "ROLE_USER")       // 내용: 권한 추가
                .issuedAt(new Date())             // 발행 시간
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시간
                .signWith(secretKey)              // 서명 (비밀키로 암호화)
                .compact();
    }

    // 2. 토큰에서 이메일(Subject) 추출 (인증할 때 사용)
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 3. 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 만료되었거나, 조작되었거나, 형식이 잘못된 경우
            return false;
        }
    }
}