package com.ssafy.travelog.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}") // properties에서 경로 가져옴
    private String uploadDir;

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // 1. 원본 파일명에서 확장자 추출 (.jpg, .png 등)
        String originalFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 2. 저장될 새 파일명 (UUID + 확장자) -> 겹침 방지
        String savedFilename = uuid + extension;

        // 3. 실제 저장 경로 생성
        File dest = new File(uploadDir + savedFilename);

        // 폴더가 없으면 생성
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        // 4. 파일 저장
        file.transferTo(dest);

        // 5. 접근 가능한 URL 반환 (예: /images/uuid.jpg)
        return "/images/" + savedFilename;
    }

    /**
     * 여러 파일을 업로드하고 URL 리스트 반환
     */
    public List<String> uploadMultipleFiles(List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String url = uploadFile(file);
                if (url != null) {
                    urls.add(url);
                }
            }
        }

        return urls;
    }
}