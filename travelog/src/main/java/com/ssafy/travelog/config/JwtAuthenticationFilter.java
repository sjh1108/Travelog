package com.ssafy.travelog.config;

import com.ssafy.travelog.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 헤더에서 Authorization 키의 값을 가져옴
        String header = request.getHeader("Authorization");

        // 2. 헤더가 없거나 "Bearer "로 시작하지 않으면 그냥 통과 (인증 실패 상태로 다음 필터로 넘어감)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. "Bearer " 떼고 토큰만 추출
            String token = header.substring(7);

            // 4. 토큰 검증
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmail(token);

                // [수정 포인트] 토큰에서 role 정보 꺼내기!
                String role = jwtUtil.getRole(token);

                // 혹시 role이 없을 경우를 대비해 기본값 설정 (선택사항)
                if (role == null) role = "ROLE_USER";

                // 5. 유효하다면 Spring Security에 인증 정보 저장
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        // [수정 포인트] 하드코딩("ROLE_USER") 대신 가져온 role 변수 사용
                        List.of(new SimpleGrantedAuthority(role))
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("인증 성공: {} / 권한: {}", email, role);
            }
        } catch (Exception e) {
            log.error("인증 실패: {}", e.getMessage());
        }

        // 6. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}