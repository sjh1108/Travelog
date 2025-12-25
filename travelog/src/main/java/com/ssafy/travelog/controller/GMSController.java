package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.service.GMSService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gms")
@RequiredArgsConstructor
@Tag(name = "GMS 컨트롤러", description = "AI 여행 추천 챗봇")
public class GMSController {

    private final GMSService gmsService;

    @Operation(summary = "AI 여행 추천 받기", description = "사용자의 여행 기록과 원하는 테마를 기반으로 AI가 여행지를 추천합니다")
    @GetMapping
    public String chat(
            @RequestParam(value = "history", required = false) List<String> histories,
            @RequestParam(value = "theme", required = false) List<String> themes) {

        return gmsService.sendMessage(histories, themes);
    }
}