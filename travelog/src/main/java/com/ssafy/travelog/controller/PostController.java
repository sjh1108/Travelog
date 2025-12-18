package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.dto.PostDto;
import com.ssafy.travelog.model.dto.UserDto;
import com.ssafy.travelog.model.mapper.UserMapper;
import com.ssafy.travelog.model.service.PostService;
import com.ssafy.travelog.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Post 컨트롤러", description = "게시물 작성, 조회")
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper; // 원래는 UserService 통해야 하는데 테스트용으로 직접 사용

    @Operation(summary = "게시물 작성")
    @PostMapping
    public ResponseEntity<?> write(
            @RequestHeader("Authorization") String header, // 헤더에서 토큰 꺼냄
            @RequestBody PostDto postDto) {
        try {
            // 1. 토큰 검증 및 유저 찾기
            String token = header.substring(7); // "Bearer " 제거
            String email = jwtUtil.getEmail(token);
            UserDto user = userMapper.selectUserByEmail(email);

            // 2. 작성자 ID 주입
            postDto.setUserId(user.getId());

            // 3. 저장
            log.info("게시물 작성 요청: {}", postDto);
            postService.writePost(postDto);

            return new ResponseEntity<>("게시물 작성 성공", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("게시물 작성 실패", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "전체 게시물 조회")
    @GetMapping
    public ResponseEntity<?> list() {
        List<PostDto> list = postService.getList();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}