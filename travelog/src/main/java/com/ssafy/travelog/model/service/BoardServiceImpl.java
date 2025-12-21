package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.BoardDto;
import com.ssafy.travelog.model.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    @Override
    public void writePost(BoardDto boardDto) {
        // 나중에 여기서 유효성 검사 등을 추가할 수 있음
        boardMapper.insertBoard(boardDto);
    }

    @Override
    public List<BoardDto> listPosts() {
        return boardMapper.listPosts();
    }

    @Override
    public List<BoardDto> getPostList(String keyword) {
        return boardMapper.selectPosts(keyword);
    }
}