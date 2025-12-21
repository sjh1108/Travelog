package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File 컨트롤러", description = "이미지 업로드")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "이미지 업로드 (URL 반환)")
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileService.uploadFile(file);
            log.info("이미지 업로드 성공: {}", imageUrl);
            return new ResponseEntity<>(imageUrl, HttpStatus.OK);
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            return new ResponseEntity<>("업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}