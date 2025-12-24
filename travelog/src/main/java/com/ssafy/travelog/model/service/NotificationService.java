package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.NotificationDto;
import java.util.List;

public interface NotificationService {
    // 알림 생성
    void createNotification(NotificationDto notificationDto);

    // 특정 사용자의 알림 목록 조회
    List<NotificationDto> getNotifications(String userEmail);

    // 알림 읽음 처리
    void markAsRead(int id);

    // 알림 전체 읽음 처리
    void markAllAsRead(String userEmail);

    // 알림 삭제
    void deleteNotification(int id);

    // 읽지 않은 알림 개수
    int getUnreadCount(String userEmail);
}
