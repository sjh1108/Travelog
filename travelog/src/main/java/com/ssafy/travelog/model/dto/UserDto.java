package com.ssafy.travelog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "회원 정보 DTO")
public class UserDto {

    @Schema(description = "회원 고유 ID (PK)", hidden = true)
    private int userId; // DB: user_id

    @Schema(description = "이메일 (아이디)", example = "ssafy@ssafy.com")
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    private String password;

    @Schema(description = "닉네임", example = "김싸피")
    private String nickname;

    @Schema(description = "프로필 이미지 URL")
    private String profileImage;

    @Schema(description = "권한 (ROLE_USER, ROLE_ADMIN)", hidden = true)
    private String role;

    @Schema(description = "가입일", hidden = true)
    private String createdAt;
}