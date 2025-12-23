package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.gms.ChatRequest;
import com.ssafy.travelog.model.dto.gms.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class GMSServiceImpl implements GMSService{

    private final RestClient restClient;

    @Override
    public String getChatResponse(String userPrompt) {
        ChatRequest request = ChatRequest.of(userPrompt);

        ChatResponse response = restClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        // 응답이 없거나 에러 발생 시 처리 로직 추가 가능
        if (response == null || response.choices().isEmpty()) {
            return "No response";
        }

        return response.choices().get(0).message().content();
    }
}
