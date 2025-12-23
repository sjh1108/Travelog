package com.ssafy.travelog.model.dto.gms;

import java.util.List;

public record ChatRequest(String model, List<Message> messages) {
    // 편의 메서드
    public static ChatRequest of(String content) {
        return new ChatRequest("gpt-4o-mini",
                List.of(
                        new Message("system", "You are a helpful assistant."),
                        new Message("user", content)
                )
        );
    }
}
