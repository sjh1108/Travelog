package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.BoardDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    // DB에 게시글 저장
    void insertBoard(BoardDto boardDto);
    List<BoardDto> listPosts();
    // 파라미터 추가
    List<BoardDto> selectPosts(String keyword);
}