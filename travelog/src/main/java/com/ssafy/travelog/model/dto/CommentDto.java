package com.ssafy.travelog.model.dto;

import lombok.Data;

@Data
public class CommentDto {
    private int id;
    private int postId;          // 어느 게시글인지
    private String userEmail;    // 작성자 이메일 (입력용)
    private String nickname;     // 작성자 닉네임 (화면 표시용)
    private String profileImage; // 작성자 프로필 이미지 (화면 표시용)
    private String content;      // 댓글 내용
    private String createdAt;    // 작성일
}