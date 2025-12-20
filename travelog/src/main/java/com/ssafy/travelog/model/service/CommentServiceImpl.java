package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.CommentDto;
import com.ssafy.travelog.model.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    public void writeComment(CommentDto commentDto) {
        commentMapper.insertComment(commentDto);
    }

    @Override
    public List<CommentDto> getComments(int postId) {
        return commentMapper.selectCommentsByPostId(postId);
    }

    @Override
    public void deleteComment(int id, String userEmail) {
        commentMapper.deleteComment(id, userEmail);
    }
}