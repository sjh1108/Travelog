package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.BoardDto;

import java.util.List;

public interface BoardService {
    void writePost(BoardDto boardDto);
//     기존 list() 메서드 시그니처 변경
//    List<BoardDto> listPosts();
    List<BoardDto> getPostList(Integer lastId, int size);

}