package com.ssafy.travelog.model.dto;

import lombok.Data;

@Data
public class LikeDto {
    private int postId;
    private String userEmail;
}