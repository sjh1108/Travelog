package com.ssafy.travelog.exception;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp; // 에러 발생 시간
    private int status;              // HTTP 상태 코드 (400, 404, 500 등)
    private String error;            // 에러 이름 (Bad Request 등)
    private String message;          // 상세 메시지 (아이디가 중복됩니다 등)
    private String path;             // 요청한 URL
}