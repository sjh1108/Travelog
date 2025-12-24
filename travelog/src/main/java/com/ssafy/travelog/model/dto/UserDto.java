package com.ssafy.travelog.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    private int id;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이여야 합니다.")
    private String nickname;

    // 팔로우 관련 필드 (DB 저장 X, 조회 시 동적으로 계산)
    private Integer followerCount;  // 팔로워 수 (이 사람을 팔로우하는 사람들의 수)
    private Integer followingCount; // 팔로잉 수 (이 사람이 팔로우하는 사람들의 수)

    private String profileImage;
    private String bio;
    private String role;
}