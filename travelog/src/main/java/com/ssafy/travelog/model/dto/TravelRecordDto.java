package com.ssafy.travelog.model.dto;

import lombok.Data;
import java.math.BigDecimal; // 금액 계산용

@Data
public class TravelRecordDto {
    private int id;
    private String userEmail;     // 작성자 이메일 (입력/출력용)
    private String nickname;      // 작성자 닉네임 (출력용)

    private String title;         // 여행 제목
    private String startDate;     // 시작일 (YYYY-MM-DD)
    private String endDate;       // 종료일 (YYYY-MM-DD)
    private BigDecimal totalCost; // 총 경비
    private String theme;         // 여행 테마 (예: 힐링, 먹방)
    private boolean isPublic;     // 공개 여부 (true/false)

    private String createdAt;     // 생성일
}