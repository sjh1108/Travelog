package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.BoardDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    // DB에 게시글 저장
    void insertBoard(BoardDto boardDto);
//    List<BoardDto> listPosts();
//     무한 스크롤 조회 메서드 추가
//     파라미터가 2개 이상일 땐 @Param을 쓰거나 Map을 써야 하는데,
//     여기선 간단하게 Map 대신 DTO를 쓰거나 MyBatis의 자동 매핑을 이용합니다.
//     (MyBatis 버전에 따라 파라미터 이름 그대로 사용 가능)
    List<BoardDto> selectPostsScroll(Integer lastId, int size);
}