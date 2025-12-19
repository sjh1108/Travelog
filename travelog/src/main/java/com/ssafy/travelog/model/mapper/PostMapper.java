package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.PostDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PostMapper {
    // 게시물 작성
    void insertPost(PostDto postDto);

    // 전체 게시물 조회 (최신순)
    List<PostDto> selectAllPosts();

    // 특정 유저의 게시물 조회
    List<PostDto> selectPostsByUserId(int userId);
}