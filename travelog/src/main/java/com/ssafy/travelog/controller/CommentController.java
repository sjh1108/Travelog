package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.dto.CommentDto;
import com.ssafy.travelog.model.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comment 컨트롤러", description = "댓글 관리")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping
    public ResponseEntity<?> write(@RequestBody CommentDto commentDto, Authentication authentication) {
        try {
            String email = (String) authentication.getPrincipal();
            commentDto.setUserEmail(email); // 작성자 주입

            commentService.writeComment(commentDto);
            return new ResponseEntity<>("댓글 작성 성공", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("댓글 작성 실패", e);
            return new ResponseEntity<>("작성 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "게시글의 댓글 목록 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<?> list(@PathVariable int postId) {
        return new ResponseEntity<>(commentService.getComments(postId), HttpStatus.OK);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, Authentication authentication) {
        try {
            String email = (String) authentication.getPrincipal();
            commentService.deleteComment(id, email); // 이메일 넘겨서 본인 확인
            return new ResponseEntity<>("댓글 삭제 성공", HttpStatus.OK);
        } catch (Exception e) {
            log.error("댓글 삭제 실패", e);
            return new ResponseEntity<>("삭제 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}