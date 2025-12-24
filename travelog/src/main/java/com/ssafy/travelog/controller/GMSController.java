package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.service.GMSService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gms")
@RequiredArgsConstructor
public class GMSController {

    private final GMSService gmsService;

    // 예시 요청: /api/gms?history=제주도&history=스위스&theme=힐링&theme=맛집
    @GetMapping
    public String chat(
            @RequestParam(value = "history", required = false) List<String> histories,
            @RequestParam(value = "theme", required = false) List<String> themes) {

        return gmsService.sendMessage(histories, themes);
    }
}