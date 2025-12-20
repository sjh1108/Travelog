package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.LikeDto;
import com.ssafy.travelog.model.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;

    @Override
    public boolean toggleLike(LikeDto likeDto) {
        // 1. 이미 좋아요를 눌렀는지 확인
        int count = likeMapper.checkLike(likeDto);

        if (count > 0) {
            // 2. 이미 눌렀다면 -> 취소 (DELETE)
            likeMapper.unLike(likeDto);
            return false; // 좋아요 꺼짐
        } else {
            // 3. 안 눌렀다면 -> 추가 (INSERT)
            likeMapper.doLike(likeDto);
            return true; // 좋아요 켜짐
        }
    }
}