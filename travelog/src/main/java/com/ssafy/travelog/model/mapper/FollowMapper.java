package com.ssafy.travelog.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FollowMapper {
    // 팔로우 추가
    void insertFollow(@Param("followerId") int followerId, @Param("followingId") int followingId);

    // 팔로우 삭제 (언팔로우)
    void deleteFollow(@Param("followerId") int followerId, @Param("followingId") int followingId);

    // 팔로우 여부 확인
    int isFollowing(@Param("followerId") int followerId, @Param("followingId") int followingId);

    // 특정 사용자의 팔로워 수
    int getFollowerCount(@Param("userId") int userId);

    // 특정 사용자의 팔로잉 수
    int getFollowingCount(@Param("userId") int userId);
}
