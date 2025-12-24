package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.LikeDto;
import com.ssafy.travelog.model.dto.NotificationDto;
import com.ssafy.travelog.model.mapper.BoardMapper;
import com.ssafy.travelog.model.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final BoardMapper boardMapper;
    @Lazy
    private final NotificationService notificationService;

    @Override
    public boolean toggleLike(LikeDto likeDto) {
        // 1. 이미 좋아요를 눌렀는지 확인
        int count = likeMapper.checkLike(likeDto);

        if (count > 0) {
            // 2. 이미 눌렀다면 -> 취소 (DELETE)
            likeMapper.unLike(likeDto);
            return false; // 좋아요 꺼짐
        } else {
            // 3. 안 눌렀다면 -> 추가 (INSERT)
            likeMapper.doLike(likeDto);

            // 4. 알림 생성 (게시물 작성자가 자기 자신이 아닌 경우에만)
            try {
                String postWriterEmail = boardMapper.getPostWriterEmail(likeDto.getPostId());
                log.info("좋아요 알림 생성 시도 - 게시물 작성자: {}, 좋아요 누른 사람: {}", postWriterEmail, likeDto.getUserEmail());

                // 자기 자신의 게시물이 아닌 경우에만 알림 생성
                if (postWriterEmail != null && !postWriterEmail.equals(likeDto.getUserEmail())) {
                    NotificationDto notification = new NotificationDto();
                    notification.setUserEmail(postWriterEmail);  // 알림 받을 사람 (게시물 작성자)
                    notification.setActorEmail(likeDto.getUserEmail());  // 좋아요 누른 사람
                    notification.setType("like");
                    notification.setPostId(likeDto.getPostId());
                    notificationService.createNotification(notification);
                    log.info("좋아요 알림 생성 완료 - postId: {}", likeDto.getPostId());
                } else {
                    log.info("자기 자신의 게시물이므로 알림 생성하지 않음");
                }
            } catch (Exception e) {
                // 알림 생성 실패해도 좋아요 기능은 정상 동작
                log.error("알림 생성 실패: {}", e.getMessage(), e);
            }

            return true; // 좋아요 켜짐
        }
    }

    @Override
    public int getLikeCount(int postId) {
        return likeMapper.getLikeCount(postId);
    }

    @Override
    public boolean isLikedByUser(int postId, String userEmail) {
        LikeDto likeDto = new LikeDto();
        likeDto.setPostId(postId);
        likeDto.setUserEmail(userEmail);
        return likeMapper.checkLike(likeDto) > 0;
    }
}