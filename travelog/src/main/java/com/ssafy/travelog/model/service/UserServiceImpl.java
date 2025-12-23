package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.UserDto;
import com.ssafy.travelog.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder; // SecurityConfig에서 주입받음

    @Override
    @Transactional
    public void join(UserDto userDto) throws Exception {
        // 1. 이메일 중복 검사
        if (userMapper.existsByEmail(userDto.getEmail()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. bio가 없으면 기본값 설정
        if (userDto.getBio() == null || userDto.getBio().trim().isEmpty()) {
            userDto.setBio(userDto.getNickname() + "입니다.");
        }

        // 3. 비밀번호 암호화 (보안 필수!)
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);

        // 4. DB 저장
        userMapper.insertUser(userDto);
    }

    @Override
    public UserDto login(UserDto userDto) throws Exception {
        // 1. 이메일로 회원 조회
        UserDto loginUser = userMapper.selectUserByEmail(userDto.getEmail());

        // 2. 회원이 없거나 비밀번호가 틀리면 에러
        if (loginUser == null || !passwordEncoder.matches(userDto.getPassword(), loginUser.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        return loginUser; // 로그인 성공 시 회원정보 반환
    }

    @Override
    @Transactional
    public void updateUser(UserDto userDto) throws Exception {
        // 1. 회원 존재 여부 확인
        if (userMapper.existsByEmail(userDto.getEmail()) == 0) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        // 2. 닉네임 중복 검사 (본인 닉네임이 아닌 경우만)
        UserDto currentUser = userMapper.selectUserByEmail(userDto.getEmail());
        if (userDto.getNickname() != null &&
            !userDto.getNickname().equals(currentUser.getNickname()) &&
            userMapper.existsByNickname(userDto.getNickname()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 3. DB 업데이트
        userMapper.updateUser(userDto);
    }

    @Override
    public UserDto getUserByEmail(String email) throws Exception {
        UserDto user = userMapper.selectUserByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(String email) throws Exception {
        // 1. 회원 존재 여부 확인
        if (userMapper.existsByEmail(email) == 0) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        // 2. 사용자의 댓글 삭제 (트리거가 자동으로 comment_count 감소)
        userMapper.deleteUserComments(email);

        // 3. 사용자의 좋아요 삭제 (트리거가 자동으로 like_count 감소)
        userMapper.deleteUserLikes(email);

        // 4. 사용자 삭제
        userMapper.deleteUser(email);
    }
}