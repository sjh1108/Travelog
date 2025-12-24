package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.dto.UserDto;
import com.ssafy.travelog.model.service.UserService;
import com.ssafy.travelog.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> join(@Valid @RequestBody UserDto userDto) {
        try {
            log.debug("회원가입 요청: {}", userDto);
            userService.join(userDto);

            // 회원가입 후 자동 로그인을 위한 토큰 발급
            String token = jwtUtil.createToken(userDto.getEmail(), userDto.getNickname());

            Map<String, Object> result = new HashMap<>();
            result.put("message", "회원가입 성공");
            result.put("accessToken", token);
            result.put("nickname", userDto.getNickname());
            result.put("profileImage", userDto.getProfileImage());
            result.put("bio", userDto.getBio());

            log.info("회원가입 성공 및 토큰 발급: {}", userDto.getEmail());
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("회원가입 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        try {
            UserDto loginUser = userService.login(userDto);
            String token = jwtUtil.createToken(loginUser.getEmail(), loginUser.getNickname(), loginUser.getRole());

            Map<String, Object> result = new HashMap<>();
            result.put("message", "로그인 성공");
            result.put("accessToken", token);
            result.put("nickname", loginUser.getNickname());
            result.put("profileImage", loginUser.getProfileImage());
            result.put("bio", loginUser.getBio());

            log.info("로그인 성공 및 토큰 발급: {}", loginUser.getEmail());
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            log.error("로그인 실패", e);
            return new ResponseEntity<>("아이디 또는 비밀번호를 확인하세요.", HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "회원정보 수정")
    @PutMapping("/me")
    public ResponseEntity<?> updateUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UserDto userDto) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtil.getEmail(jwtToken);

            log.debug("회원정보 수정 요청: {}", email);
            log.debug("수정할 데이터: {}", userDto);

            // 이메일 설정 (본인만 수정 가능)
            userDto.setEmail(email);

            userService.updateUser(userDto);

            // 수정된 사용자 정보 조회
            UserDto updatedUser = userService.getUserByEmail(email);

            // 응답 데이터 구성
            Map<String, Object> result = new HashMap<>();
            result.put("message", "회원정보 수정 성공");
            result.put("nickname", updatedUser.getNickname());
            result.put("profileImage", updatedUser.getProfileImage());
            result.put("bio", updatedUser.getBio());

            log.info("회원정보 수정 성공: {}", email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("회원정보 수정 실패: {}", e.getMessage());
            return new ResponseEntity<>("회원정보 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            // Bearer 토큰에서 실제 토큰 추출
            String jwtToken = token.replace("Bearer ", "");

            // 토큰에서 이메일 추출
            String email = jwtUtil.getEmail(jwtToken);

            log.debug("회원탈퇴 요청: {}", email);
            userService.deleteUser(email);

            log.info("회원탈퇴 성공: {}", email);
            return new ResponseEntity<>("회원탈퇴가 완료되었습니다.", HttpStatus.OK);

        } catch (Exception e) {
            log.error("회원탈퇴 실패: {}", e.getMessage());
            return new ResponseEntity<>("회원탈퇴에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "내 정보 조회 (팔로워/팔로잉 수 포함)")
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtil.getEmail(jwtToken);

            log.debug("내 정보 조회: {}", email);
            UserDto user = userService.getMyInfo(email);

            Map<String, Object> result = new HashMap<>();
            result.put("id", user.getId());
            result.put("email", user.getEmail());
            result.put("nickname", user.getNickname());
            result.put("profileImage", user.getProfileImage());
            result.put("bio", user.getBio());
            result.put("followerCount", user.getFollowerCount());
            result.put("followingCount", user.getFollowingCount());

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("내 정보 조회 실패: {}", e.getMessage());
            return new ResponseEntity<>("내 정보 조회에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "특정 사용자 정보 조회 (팔로워/팔로잉 수 포함)")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable String userId) {
        try {
            log.debug("사용자 정보 조회: {}", userId);
            UserDto user = userService.getUserByIdOrEmail(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("id", user.getId());
            result.put("email", user.getEmail());
            result.put("nickname", user.getNickname());
            result.put("profileImage", user.getProfileImage());
            result.put("bio", user.getBio());
            result.put("followerCount", user.getFollowerCount());
            result.put("followingCount", user.getFollowingCount());

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("사용자 정보 조회 실패: {}", e.getMessage());
            return new ResponseEntity<>("사용자 정보 조회에 실패했습니다.", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "팔로우/언팔로우 토글")
    @PostMapping("/{userId}/follow")
    public ResponseEntity<?> toggleFollow(
            @RequestHeader("Authorization") String token,
            @PathVariable String userId) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtil.getEmail(jwtToken);

            log.debug("팔로우 토글: {} -> {}", email, userId);
            Map<String, Object> result = userService.toggleFollow(email, userId);

            log.info("팔로우 토글 성공: {} -> {}", email, userId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("팔로우 토글 실패: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("팔로우 토글 실패: {}", e.getMessage());
            return new ResponseEntity<>("팔로우 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "팔로우 상태 확인")
    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<?> checkFollowStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String userId) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String email = jwtUtil.getEmail(jwtToken);

            boolean isFollowing = userService.isFollowing(email, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("isFollowing", isFollowing);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("팔로우 상태 확인 실패: {}", e.getMessage());
            return new ResponseEntity<>("팔로우 상태 확인에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}