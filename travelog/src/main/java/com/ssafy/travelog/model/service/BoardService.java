package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.BoardDto;

import java.util.List;

public interface BoardService {
    void writePost(BoardDto boardDto);
    List<BoardDto> listPosts();
    // 파라미터 추가
    List<BoardDto> getPostList(String keyword);
}