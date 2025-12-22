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

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);

        // 3. [추가] 역할(Role) 설정
        // (만약 관리자 가입 로직이 따로 없다면, 기본적으로 ROLE_USER로 세팅)
        if (userDto.getRole() == null || userDto.getRole().isEmpty()) {
            userDto.setRole("ROLE_USER");
        }

        // 3. DB 저장
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
}