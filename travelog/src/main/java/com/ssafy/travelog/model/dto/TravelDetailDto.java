package com.ssafy.travelog.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TravelDetailDto {
    private int id;
    private int travelRecordId;    // 부모 여행 기록 ID (FK)
    private Integer touristSpotId; // 관광지 ID (없으면 null 가능)

    private int sequenceOrder;     // 여행 순서 (1, 2, 3...)
    private String locationName;   // 장소 이름 (프론트엔드 요구사항)
    private String description;    // 메모/설명 (review 대신 사용)
    private String review;         // 장소 후기 (하위 호환성 유지)
    private BigDecimal cost;       // 비용
    private String visitDate;      // 방문 날짜 (YYYY-MM-DD)
    private List<String> imageUrls; // 사진 URL 리스트
    private String photos;         // 사진 URL 리스트 (JSON 문자열로 저장, 하위 호환성 유지)

    private Double latitude;       // 위도
    private Double longitude;      // 경도
}