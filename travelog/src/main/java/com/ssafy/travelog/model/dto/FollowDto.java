package com.ssafy.travelog.model.dto;

import lombok.Data;

@Data
public class FollowDto {
    private int id;
    private int followerId;      // 팔로우하는 사용자 ID
    private int followingId;     // 팔로우받는 사용자 ID
    private String createdAt;    // 생성일
}
