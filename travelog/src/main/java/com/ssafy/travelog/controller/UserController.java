package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.dto.UserDto;
import com.ssafy.travelog.model.service.UserService;
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

    @Operation(summary = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserDto userDto) {
        try {
            log.debug("회원가입 요청: {}", userDto);
            userService.join(userDto);
            return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("회원가입 실패", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        try {
            UserDto loginUser = userService.login(userDto);

            // 원래는 여기서 JWT 토큰을 만들어야 함 (다음 단계)
            Map<String, Object> result = new HashMap<>();
            result.put("message", "로그인 성공");
            result.put("nickname", loginUser.getNickname());

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}