package com.ssafy.travelog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // [수정 포인트]
        // 1. /images/** 로 요청이 오면
        // 2. file:/// + 저장경로 에서 파일을 찾는다.
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///" + uploadDir);
        // 주의: 윈도우는 file:/// 이렇게 슬래시가 3개여야 합니다.
    }
}