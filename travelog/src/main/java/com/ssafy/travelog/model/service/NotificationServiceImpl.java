package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.NotificationDto;
import com.ssafy.travelog.model.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public void createNotification(NotificationDto notificationDto) {
        log.info("알림 생성 - userEmail: {}, actorEmail: {}, type: {}, postId: {}",
                notificationDto.getUserEmail(), notificationDto.getActorEmail(),
                notificationDto.getType(), notificationDto.getPostId());
        notificationMapper.insertNotification(notificationDto);
        log.info("알림 DB 저장 완료");
    }

    @Override
    public List<NotificationDto> getNotifications(String userEmail) {
        return notificationMapper.selectNotificationsByUserEmail(userEmail);
    }

    @Override
    public void markAsRead(int id) {
        notificationMapper.markAsRead(id);
    }

    @Override
    public void markAllAsRead(String userEmail) {
        notificationMapper.markAllAsRead(userEmail);
    }

    @Override
    public void deleteNotification(int id) {
        notificationMapper.deleteNotification(id);
    }

    @Override
    public int getUnreadCount(String userEmail) {
        log.info("NotificationService - getUnreadCount 호출, userEmail: {}", userEmail);
        try {
            int count = notificationMapper.countUnreadNotifications(userEmail);
            log.info("NotificationService - 조회 결과: {}", count);
            return count;
        } catch (Exception e) {
            log.error("NotificationService - 알림 개수 조회 중 에러", e);
            throw e;
        }
    }
}
