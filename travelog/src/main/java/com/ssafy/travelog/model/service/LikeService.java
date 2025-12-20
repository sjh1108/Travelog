package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.LikeDto;

public interface LikeService {
    // 리턴값: true면 좋아요 성공(ON), false면 좋아요 취소(OFF)
    boolean toggleLike(LikeDto likeDto);
}