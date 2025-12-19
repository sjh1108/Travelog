package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CommentMapper {
    // 댓글 작성
    void insertComment(CommentDto commentDto);

    // 특정 게시글의 댓글 목록 조회
    List<CommentDto> selectCommentsByPostId(int postId);

    // 댓글 삭제 (본인 것만 삭제 가능하도록 email 체크)
    void deleteComment(int id, String userEmail);
}