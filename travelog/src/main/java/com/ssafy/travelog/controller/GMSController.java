package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.service.GMSService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gms")
@RequiredArgsConstructor
public class GMSController {

    private final GMSService gmsService;

    @GetMapping
    public String chat(@RequestParam(value = "prompt", defaultValue = "Hello") String prompt) {
        return gmsService.getChatResponse(prompt);
    }
}
