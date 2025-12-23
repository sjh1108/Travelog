package com.ssafy.travelog.model.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.travelog.model.dto.gms.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GMSServiceImpl implements GMSService {

    @Value("${gms.key}")
    private String gmsKey;

    private final ObjectMapper objectMapper;

    @Override
    public String getChatResponse(String userPrompt) {
        ChatRequest requestDto = ChatRequest.of(userPrompt);

        // 1. 순수 Apache HttpClient 생성 (스프링 설정 무시)
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // 2. POST 요청 생성
            HttpPost httpPost = new HttpPost("https://gms.ssafy.io/gmsapi/api.openai.com/v1/chat/completions");

            // 3. 헤더 설정 (curl과 100% 동일하게)
            httpPost.setHeader("Authorization", "Bearer " + gmsKey.trim());
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("User-Agent", "curl/8.13.0"); // 위장
            httpPost.setHeader("Accept", "*/*");

            // 4. 바디 설정 (JSON 변환)
            String jsonBody = objectMapper.writeValueAsString(requestDto);
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            log.info(">>>>>> [요청 보냄] User-Agent: curl/8.13.0");

            // 5. 실행 및 응답 받기 (Raw String으로 받음)
            return httpClient.execute(httpPost, response -> {
                int status = response.getCode();
                String body = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8))
                        .lines().collect(Collectors.joining("\n"));

                log.info("<<<<<< [응답 수신] 상태코드: {}", status);

                if (status >= 400) {
                    log.error("에러 본문: {}", body);
                    throw new RuntimeException("GMS 에러: " + status + " / " + body);
                }

                // 6. JSON 파싱해서 content만 추출
                JsonNode root = objectMapper.readTree(body);
                return root.path("choices").get(0).path("message").path("content").asText();
            });

        } catch (Exception e) {
            log.error("요청 실패", e);
            throw new RuntimeException(e);
        }
    }
}