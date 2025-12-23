package com.ssafy.travelog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GMSConfig {

    @Value("${gms.key}")
    private String gmsKey;

    @Bean
    public RestClient GMSRestClient() {
        return RestClient.builder()
                .baseUrl("https://gms.ssafy.io/gmsapi/api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + gmsKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}