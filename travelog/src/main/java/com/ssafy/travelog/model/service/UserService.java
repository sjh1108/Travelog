package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.UserDto;

public interface UserService {
    // 회원가입
    void join(UserDto userDto) throws Exception;

    // 로그인 (성공 시 회원정보 리턴)
    UserDto login(UserDto userDto) throws Exception;

    // 회원정보 수정
    void updateUser(UserDto userDto) throws Exception;

    // 이메일로 회원 조회
    UserDto getUserByEmail(String email) throws Exception;

    // 회원탈퇴
    void deleteUser(String email) throws Exception;
}