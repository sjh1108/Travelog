package com.ssafy.travelog.model.dto;

import lombok.Data;

@Data
public class NotificationDto {
    private int id;
    private String userEmail;       // 알림을 받는 사용자 이메일
    private String actorEmail;      // 알림을 발생시킨 사용자 이메일
    private String type;            // like, comment, follow
    private Integer postId;         // 게시물 ID (좋아요/댓글의 경우)
    private Integer commentId;      // 댓글 ID (댓글의 경우)
    private Boolean isRead;         // 읽음 여부
    private String createdAt;       // 생성일

    // 화면 표시용 필드
    private String actorNickname;   // 알림을 발생시킨 사용자 닉네임
    private String actorProfileImage; // 알림을 발생시킨 사용자 프로필 이미지
}
