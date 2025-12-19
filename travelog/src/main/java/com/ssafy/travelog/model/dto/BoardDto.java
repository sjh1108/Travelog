package com.ssafy.travelog.model.dto;

import lombok.Data;

@Data
public class BoardDto {
    private int id;
    private String writerEmail; // 작성 (Input) 용

    // --- [추가] 조회 (Output) 용 필드 ---
    private String nickname;      // 작성자 닉네임 (JOIN으로 가져옴)
    private String profileImage;  // 작성자 프사 (JOIN으로 가져옴)
    private int likeCount;        // 좋아요 수
    private int commentCount;     // 댓글 수
    private String createdAt;     // 작성일
    // -------------------------------

    private String imageUrl;
    private String travelLocation;
    private String caption;
}