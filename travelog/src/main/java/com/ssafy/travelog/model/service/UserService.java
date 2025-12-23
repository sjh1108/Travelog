package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.UserDto;

import java.util.Map;

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

    // ID로 회원 조회 (팔로워/팔로잉 수 포함)
    UserDto getUserById(int userId) throws Exception;

    // ID 또는 이메일로 회원 조회 (팔로워/팔로잉 수 포함)
    UserDto getUserByIdOrEmail(String userIdOrEmail) throws Exception;

    // 팔로우/언팔로우 토글
    Map<String, Object> toggleFollow(String followerEmail, String followingIdOrEmail) throws Exception;

    // 팔로우 상태 확인
    boolean isFollowing(String followerEmail, String followingIdOrEmail) throws Exception;

    // 내 정보 조회 (팔로워/팔로잉 수 포함)
    UserDto getMyInfo(String email) throws Exception;
}