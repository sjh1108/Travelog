package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.PostDto;
import com.ssafy.travelog.model.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    @Override
    public void writePost(PostDto postDto) {
        postMapper.insertPost(postDto);
    }

    @Override
    public List<PostDto> getList() {
        return postMapper.selectAllPosts();
    }
}