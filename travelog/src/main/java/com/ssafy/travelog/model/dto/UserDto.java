package com.ssafy.travelog.model.dto;

import lombok.Data;

@Data
public class UserDto {
    private int id;
    private String email;
    private String password;
    private String nickname;
    private String profileImage; // DB: profile_image
    private String bio;          // DB: bio
    private String createdAt;    // DB: created_at
    private String updatedAt;    // DB: updated_at

    // 팔로우 관련 필드 (DB 저장 X, 조회 시 동적으로 계산)
    private Integer followerCount;  // 팔로워 수 (이 사람을 팔로우하는 사람들의 수)
    private Integer followingCount; // 팔로잉 수 (이 사람이 팔로우하는 사람들의 수)

    // Spring Security를 위한 임시 필드 (DB 저장 X)
    private String role = "ROLE_USER";
}