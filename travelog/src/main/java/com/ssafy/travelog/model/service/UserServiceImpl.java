package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.NotificationDto;
import com.ssafy.travelog.model.dto.UserDto;
import com.ssafy.travelog.model.mapper.FollowMapper;
import com.ssafy.travelog.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final FollowMapper followMapper;
    private final NotificationService notificationService;
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

        // 4. 사용자 삭제 (CASCADE로 팔로우 관계도 자동 삭제)
        userMapper.deleteUser(email);
    }

    @Override
    public UserDto getUserById(int userId) throws Exception {
        UserDto user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        // 팔로워/팔로잉 수 설정
        user.setFollowerCount(followMapper.getFollowerCount(userId));
        user.setFollowingCount(followMapper.getFollowingCount(userId));

        return user;
    }

    @Override
    public UserDto getUserByIdOrEmail(String userIdOrEmail) throws Exception {
        UserDto user = null;

        // 숫자인지 확인 (ID)
        try {
            int userId = Integer.parseInt(userIdOrEmail);
            user = userMapper.selectUserById(userId);
        } catch (NumberFormatException e) {
            // 숫자가 아니면 이메일로 간주
            user = userMapper.selectUserByEmail(userIdOrEmail);
        }

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        // 팔로워/팔로잉 수 설정
        user.setFollowerCount(followMapper.getFollowerCount(user.getId()));
        user.setFollowingCount(followMapper.getFollowingCount(user.getId()));

        return user;
    }

    @Override
    @Transactional
    public Map<String, Object> toggleFollow(String followerEmail, String followingIdOrEmail) throws Exception {
        // 1. 팔로우하는 사용자 조회
        UserDto follower = userMapper.selectUserByEmail(followerEmail);
        if (follower == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        // 2. 팔로우받는 사용자 조회 (ID 또는 이메일)
        UserDto following = null;
        try {
            int followingId = Integer.parseInt(followingIdOrEmail);
            following = userMapper.selectUserById(followingId);
        } catch (NumberFormatException e) {
            following = userMapper.selectUserByEmail(followingIdOrEmail);
        }

        if (following == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 3. 자기 자신을 팔로우하는지 확인
        if (follower.getId() == following.getId()) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        // 4. 현재 팔로우 상태 확인
        boolean isCurrentlyFollowing = followMapper.isFollowing(follower.getId(), following.getId()) > 0;

        // 5. 팔로우/언팔로우 처리
        if (isCurrentlyFollowing) {
            // 언팔로우
            followMapper.deleteFollow(follower.getId(), following.getId());
        } else {
            // 팔로우
            followMapper.insertFollow(follower.getId(), following.getId());

            // 팔로우 알림 생성
            NotificationDto notification = new NotificationDto();
            notification.setUserEmail(following.getEmail());  // 알림을 받는 사람
            notification.setActorEmail(follower.getEmail());  // 알림을 발생시킨 사람
            notification.setType("follow");
            notification.setIsRead(false);

            notificationService.createNotification(notification);
        }

        // 6. 업데이트된 팔로워/팔로잉 수 조회
        int followerCount = followMapper.getFollowerCount(following.getId());
        int followingCount = followMapper.getFollowingCount(follower.getId());

        // 7. 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("isFollowing", !isCurrentlyFollowing);
        result.put("followerCount", followerCount);
        result.put("followingCount", followingCount);

        return result;
    }

    @Override
    public boolean isFollowing(String followerEmail, String followingIdOrEmail) throws Exception {
        UserDto follower = userMapper.selectUserByEmail(followerEmail);
        if (follower == null) {
            return false;
        }

        // 팔로우받는 사용자 조회 (ID 또는 이메일)
        UserDto following = null;
        try {
            int followingId = Integer.parseInt(followingIdOrEmail);
            following = userMapper.selectUserById(followingId);
        } catch (NumberFormatException e) {
            following = userMapper.selectUserByEmail(followingIdOrEmail);
        }

        if (following == null) {
            return false;
        }

        return followMapper.isFollowing(follower.getId(), following.getId()) > 0;
    }

    @Override
    public UserDto getMyInfo(String email) throws Exception {
        UserDto user = getUserByEmail(email);

        // 팔로워/팔로잉 수 설정
        user.setFollowerCount(followMapper.getFollowerCount(user.getId()));
        user.setFollowingCount(followMapper.getFollowingCount(user.getId()));

        return user;
    }
}