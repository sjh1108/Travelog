package com.ssafy.travelog.model.dto.gms;

import java.util.List;

// 응답 JSON 구조에 맞춤 (record 안에 record 중첩 가능)
public record ChatResponse(List<Choice> choices) {
    public record Choice(Message message) {}
}