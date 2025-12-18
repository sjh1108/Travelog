package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.PostDto;
import java.util.List;

public interface PostService {
    void writePost(PostDto postDto);
    List<PostDto> getList();
}