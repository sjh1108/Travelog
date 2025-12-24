package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.LikeDto;

public interface LikeService {
    // 리턴값: true면 좋아요 성공(ON), false면 좋아요 취소(OFF)
    boolean toggleLike(LikeDto likeDto);

    // 게시물의 좋아요 수 조회
    int getLikeCount(int postId);

    // 특정 사용자가 특정 게시물에 좋아요 했는지 확인
    boolean isLikedByUser(int postId, String userEmail);
}