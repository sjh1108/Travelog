package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.dto.LikeDto;
import com.ssafy.travelog.model.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Like 컨트롤러", description = "좋아요 기능")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 토글 (누르면 ON/OFF)")
    @PostMapping("/{postId}")
    public ResponseEntity<?> toggleLike(@PathVariable int postId, Authentication authentication) {
        try {
            String email = (String) authentication.getPrincipal();

            LikeDto likeDto = new LikeDto();
            likeDto.setPostId(postId);
            likeDto.setUserEmail(email);

            boolean isLiked = likeService.toggleLike(likeDto);

            String message = isLiked ? "좋아요 완료" : "좋아요 취소";
            return new ResponseEntity<>(message, HttpStatus.OK);

        } catch (Exception e) {
            log.error("좋아요 처리 실패", e);
            return new ResponseEntity<>("오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}