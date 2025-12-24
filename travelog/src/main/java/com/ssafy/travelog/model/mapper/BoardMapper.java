package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.BoardDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    // DB에 게시글 저장
    void insertBoard(BoardDto boardDto);
    List<BoardDto> listPosts();

    // 특정 게시물의 작성자 이메일 조회
    String getPostWriterEmail(int postId);

    // 게시물 삭제 (본인 것만 삭제 가능하도록 email 체크)
    void deletePost(int postId, String writerEmail);
}