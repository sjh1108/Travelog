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

                // 5. 유효하다면 Spring Security에 "이 사람 인증됨!" 도장 찍어주기
                // (원래는 DB에서 UserDetails를 가져와야 하지만, 성능을 위해 여기서는 간단히 처리)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")) // 권한 부여
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("인증 성공: {}", email);
            }
        } catch (Exception e) {
            log.error("인증 실패: {}", e.getMessage());
        }

        // 6. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}