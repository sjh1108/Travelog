package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    // 회원가입 (프로필 이미지, 바이오 추가)
    void insertUser(UserDto userDto);

    // 이메일로 회원 조회
    UserDto selectUserByEmail(String email);

    // 회원정보 수정
    void updateUser(UserDto userDto);

    // 이메일 중복 체크
    int existsByEmail(String email);

    // 닉네임 중복 체크
    int existsByNickname(String nickname);

    // 회원의 댓글 삭제 (트리거가 자동으로 comment_count 감소)
    void deleteUserComments(String email);

    // 회원의 좋아요 삭제 (트리거가 자동으로 like_count 감소)
    void deleteUserLikes(String email);

    // 회원 삭제
    int deleteUser(String email);

    // ID로 회원 조회
    UserDto selectUserById(int userId);
}