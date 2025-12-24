package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.NotificationDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface NotificationMapper {
    // 알림 생성
    void insertNotification(NotificationDto notificationDto);

    // 특정 사용자의 알림 목록 조회 (읽지 않은 것 우선, 최신순)
    List<NotificationDto> selectNotificationsByUserEmail(String userEmail);

    // 알림 읽음 처리
    void markAsRead(int id);

    // 알림 전체 읽음 처리
    void markAllAsRead(String userEmail);

    // 알림 삭제
    void deleteNotification(int id);

    // 읽지 않은 알림 개수
    int countUnreadNotifications(String userEmail);
}
