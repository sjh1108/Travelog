package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
// 1. "가짜 DB(H2) 쓰지 말고, 내가 설정한 DB(MySQL) 써라" 라는 뜻
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// 2. 혹시 설정 파일을 못 읽을까봐 강제로 위치 지정
@TestPropertySource(locations = "classpath:application.properties")
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("회원가입 및 조회 테스트")
    void testJoinAndSelect() {
        // given
        UserDto user = new UserDto();
        user.setEmail("test2@ssafy.com"); // PK 중복 방지를 위해 이메일 변경
        user.setPassword("1234");
        user.setNickname("테스트유저");

        // when
        userMapper.insertUser(user);
        UserDto findUser = userMapper.selectUserByEmail("test2@ssafy.com");

        // then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getNickname()).isEqualTo("테스트유저");
        assertThat(findUser.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("이메일 중복 체크 테스트")
    void testExistsByEmail() {
        // given
        UserDto user = new UserDto();
        user.setEmail("dup@ssafy.com");
        user.setPassword("1111");
        user.setNickname("중복이");

        try {
            userMapper.insertUser(user);
        } catch (Exception e) {
            // 이미 존재하면 무시
        }

        // when
        int count = userMapper.existsByEmail("dup@ssafy.com");

        // then
        assertThat(count).isGreaterThanOrEqualTo(1);
    }
}