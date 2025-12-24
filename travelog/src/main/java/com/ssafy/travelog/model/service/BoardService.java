package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.BoardDto;

import java.util.List;

public interface BoardService {
    void writePost(BoardDto boardDto);
    List<BoardDto> listPosts();
    void deletePost(int postId, String writerEmail);
}