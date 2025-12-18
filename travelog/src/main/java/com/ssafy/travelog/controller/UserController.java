package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.dto.UserDto;
import com.ssafy.travelog.model.service.UserService;
import com.ssafy.travelog.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User 컨트롤러", description = "회원가입, 로그인 등")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserDto userDto) {
        try {
            log.debug("회원가입 요청: {}", userDto);
            userService.join(userDto);
            return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("회원가입 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        try {
            // 1. 서비스에서 로그인 처리 (ID/PW 검증)
            UserDto loginUser = userService.login(userDto);

            // 2. JWT 토큰 생성 (수정된 부분)
            // 첫 번째 인자: 저장할 데이터의 키 이름 ("email")
            // 두 번째 인자: 실제 데이터 값 (유저의 이메일)
            String token = jwtUtil.createToken("email", loginUser.getEmail());

            // 3. 응답 데이터 구성
            Map<String, Object> result = new HashMap<>();
            result.put("message", "로그인 성공");
            result.put("accessToken", token);
            result.put("nickname", loginUser.getNickname());

            log.info("로그인 성공 및 토큰 발급: {}", loginUser.getEmail());
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            return new ResponseEntity<>("아이디 또는 비밀번호를 확인하세요.", HttpStatus.UNAUTHORIZED);
        }
    }
}