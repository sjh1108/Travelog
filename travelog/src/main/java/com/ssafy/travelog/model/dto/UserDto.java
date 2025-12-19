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

    // Spring Security를 위한 임시 필드 (DB 저장 X)
    private String role = "ROLE_USER";
}