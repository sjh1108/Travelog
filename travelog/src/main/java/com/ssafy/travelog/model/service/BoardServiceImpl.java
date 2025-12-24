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

//    @Override
//    public List<BoardDto> listPosts() {
//        return boardMapper.listPosts();
//    }
    @Override
    public List<BoardDto> getPostList(Integer lastId, int size) {
        // lastId가 null이면, SQL 처리를 위해 아주 큰 숫자(사실상 무한대) 혹은 null 처리
        // 여기서는 Mapper XML에서 <if>로 처리하므로 그대로 넘깁니다.
        return boardMapper.selectPostsScroll(lastId, size);
    }
}