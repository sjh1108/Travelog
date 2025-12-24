package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.dto.BoardDto;
import com.ssafy.travelog.model.service.BoardService;
import com.ssafy.travelog.model.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts") // .http 파일의 요청 주소와 일치시킴
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Board 컨트롤러", description = "여행 기록 게시판")
public class BoardController {

    private final BoardService boardService;
    private final LikeService likeService;

    @Operation(summary = "게시글 작성")
    @PostMapping
    public ResponseEntity<?> writePost(@RequestBody BoardDto boardDto, Authentication authentication) {
        // 1. 로그인한 사용자 정보 꺼내기 (JwtFilter에서 넣어둔 정보)
        // authentication.getPrincipal()은 우리가 필터에서 넣은 email(String)입니다.
        String email = (String) authentication.getPrincipal();

        log.info("게시글 작성 요청자: {}", email);
        log.info("게시글 내용: {}", boardDto);

        // 2. DTO에 작성자 정보 주입
        boardDto.setWriterEmail(email);

        try {
            // 3. 서비스 호출 (DB 저장)
            boardService.writePost(boardDto);
            return new ResponseEntity<>("게시글 작성 성공", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("게시글 작성 실패", e);
            return new ResponseEntity<>("작성 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "게시글 전체 조회")
    @GetMapping
    public ResponseEntity<?> listPosts(Authentication authentication) {
        try {
            List<BoardDto> list = boardService.listPosts();
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 데이터가 없을 때 204
            }

            // 로그인한 사용자의 좋아요 여부 추가
            if (authentication != null) {
                String email = (String) authentication.getPrincipal();
                log.info("로그인한 사용자: {}", email);
                for (BoardDto board : list) {
                    boolean isLiked = likeService.isLikedByUser(board.getId(), email);
                    board.setIsLiked(isLiked);
                    log.info("게시물 ID: {}, isLiked: {}, likeCount: {}", board.getId(), isLiked, board.getLikeCount());
                }
            } else {
                // 로그인하지 않은 경우 모두 false
                log.info("로그인하지 않은 사용자");
                for (BoardDto board : list) {
                    board.setIsLiked(false);
                    log.info("게시물 ID: {}, isLiked: false, likeCount: {}", board.getId(), board.getLikeCount());
                }
            }

            return new ResponseEntity<>(list, HttpStatus.OK); // 200 OK
        } catch (Exception e) {
            log.error("게시글 목록 조회 실패", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}