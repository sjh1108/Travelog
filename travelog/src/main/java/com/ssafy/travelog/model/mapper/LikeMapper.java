package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.LikeDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper {
    // 좋아요 상태 확인 (1이면 이미 누름, 0이면 안 누름)
    int checkLike(LikeDto likeDto);

    // 좋아요 추가 (INSERT)
    void doLike(LikeDto likeDto);

    // 좋아요 취소 (DELETE)
    void unLike(LikeDto likeDto);

    // 게시물의 좋아요 수 조회
    int getLikeCount(int postId);
}