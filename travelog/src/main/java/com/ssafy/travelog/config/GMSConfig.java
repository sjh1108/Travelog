package com.ssafy.travelog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class GMSConfig {

    @Value("${gms.key}")
    private String gmsKey;

    @Bean
    public RestClient GMSRestClient() {
        // [핵심] Apache 라이브러리 대신 가장 단순한 JDK 기본 연결 사용
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(10000);
        factory.setReadTimeout(10000);

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl("https://gms.ssafy.io/gmsapi/api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + gmsKey.trim())
                .defaultHeader("Content-Type", "application/json")
                // 성공했던 curl의 명찰을 그대로 답니다
                .defaultHeader("User-Agent", "curl/8.13.0")
                .defaultHeader("Accept", "*/*")
                .build();
    }
}