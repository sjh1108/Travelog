package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.CommentDto;
import com.ssafy.travelog.model.dto.NotificationDto;
import com.ssafy.travelog.model.mapper.BoardMapper;
import com.ssafy.travelog.model.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final BoardMapper boardMapper;
    @Lazy
    private final NotificationService notificationService;

    @Override
    public void writeComment(CommentDto commentDto) {
        commentMapper.insertComment(commentDto);

        // 알림 생성 (게시물 작성자가 자기 자신이 아닌 경우에만)
        try {
            String postWriterEmail = boardMapper.getPostWriterEmail(commentDto.getPostId());
            log.info("댓글 알림 생성 시도 - 게시물 작성자: {}, 댓글 작성자: {}", postWriterEmail, commentDto.getUserEmail());

            // 자기 자신의 게시물이 아닌 경우에만 알림 생성
            if (postWriterEmail != null && !postWriterEmail.equals(commentDto.getUserEmail())) {
                NotificationDto notification = new NotificationDto();
                notification.setUserEmail(postWriterEmail);  // 알림 받을 사람 (게시물 작성자)
                notification.setActorEmail(commentDto.getUserEmail());  // 댓글 작성자
                notification.setType("comment");
                notification.setPostId(commentDto.getPostId());
                // commentId는 auto increment이므로 여기서는 null로 둠
                notificationService.createNotification(notification);
                log.info("댓글 알림 생성 완료 - postId: {}", commentDto.getPostId());
            } else {
                log.info("자기 자신의 게시물이므로 알림 생성하지 않음");
            }
        } catch (Exception e) {
            // 알림 생성 실패해도 댓글 기능은 정상 동작
            log.error("알림 생성 실패: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<CommentDto> getComments(int postId) {
        return commentMapper.selectCommentsByPostId(postId);
    }

    @Override
    public void deleteComment(int id, String userEmail) {
        commentMapper.deleteComment(id, userEmail);
    }
}