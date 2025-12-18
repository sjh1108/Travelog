package com.ssafy.travelog.model.dto;

import lombok.Data;

@Data
public class PostDto {
    private int id;
    private int userId;         // 작성자 ID (DB: user_id)
    private String imageUrl;    // DB: image_url
    private String travelLocation; // DB: travel_location
    private String caption;
    private int likeCount;      // DB: like_count (트리거로 자동 관리)
    private int commentCount;   // DB: comment_count (트리거로 자동 관리)
    private String createdAt;
    private String updatedAt;

    // 조인을 위해 작성자 닉네임이나 프로필 사진을 담을 임시 필드
    private String userNickname;
    private String userProfileImage;
}