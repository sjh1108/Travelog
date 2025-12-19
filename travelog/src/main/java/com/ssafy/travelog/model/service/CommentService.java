package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.CommentDto;
import java.util.List;

public interface CommentService {
    void writeComment(CommentDto commentDto);
    List<CommentDto> getComments(int postId);
    void deleteComment(int id, String userEmail);
}