package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    // 회원가입 (프로필 이미지, 바이오 추가)
    void insertUser(UserDto userDto);

    // 이메일로 회원 조회
    UserDto selectUserByEmail(String email);

    // 이메일 중복 체크
    int existsByEmail(String email);

    // 닉네임 중복 체크
    int existsByNickname(String nickname);
}