package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.dto.NotificationDto;
import com.ssafy.travelog.model.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification 컨트롤러", description = "알림 관리")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "내 알림 목록 조회")
    @GetMapping
    public ResponseEntity<?> getNotifications(Authentication authentication) {
        try {
            String email = (String) authentication.getPrincipal();
            List<NotificationDto> notifications = notificationService.getNotifications(email);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception e) {
            log.error("알림 조회 실패", e);
            return new ResponseEntity<>("조회 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "읽지 않은 알림 개수 조회")
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(Authentication authentication) {
        try {
            log.info("읽지 않은 알림 개수 조회 시작");
            log.info("Authentication: {}", authentication);

            if (authentication == null) {
                log.error("Authentication is null");
                return new ResponseEntity<>("인증 정보가 없습니다", HttpStatus.UNAUTHORIZED);
            }

            String email = (String) authentication.getPrincipal();
            log.info("사용자 이메일: {}", email);

            int count = notificationService.getUnreadCount(email);
            log.info("읽지 않은 알림 개수: {}", count);

            Map<String, Object> result = new HashMap<>();
            result.put("count", count);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("읽지 않은 알림 개수 조회 실패", e);
            log.error("에러 메시지: {}", e.getMessage());
            log.error("스택 트레이스: ", e);
            return new ResponseEntity<>("조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "알림 읽음 처리")
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable int id) {
        try {
            notificationService.markAsRead(id);
            return new ResponseEntity<>("읽음 처리 완료", HttpStatus.OK);
        } catch (Exception e) {
            log.error("알림 읽음 처리 실패", e);
            return new ResponseEntity<>("처리 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "알림 전체 읽음 처리")
    @PatchMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Authentication authentication) {
        try {
            String email = (String) authentication.getPrincipal();
            notificationService.markAllAsRead(email);
            return new ResponseEntity<>("전체 읽음 처리 완료", HttpStatus.OK);
        } catch (Exception e) {
            log.error("알림 전체 읽음 처리 실패", e);
            return new ResponseEntity<>("처리 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "알림 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable int id) {
        try {
            notificationService.deleteNotification(id);
            return new ResponseEntity<>("알림 삭제 완료", HttpStatus.OK);
        } catch (Exception e) {
            log.error("알림 삭제 실패", e);
            return new ResponseEntity<>("삭제 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
