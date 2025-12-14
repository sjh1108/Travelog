package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    // 1. 회원가입
    void insertUser(UserDto userDto);

    // 2. 로그인용 회원 조회 (비밀번호 검증을 위해 사용)
    UserDto selectUserByEmail(String email);

    // 3. 이메일 중복 체크 (1: 존재함, 0: 사용 가능)
    int existsByEmail(String email);

    // 4. 닉네임 중복 체크
    int existsByNickname(String nickname);
}