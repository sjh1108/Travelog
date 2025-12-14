package com.ssafy.travelog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "여행 상세 경로 DTO")
public class LogDetailDto {
    @Schema(description = "상세 ID (PK)", hidden = true)
    private int detailId;

    @Schema(description = "부모 로그 ID")
    private int logId;

    @Schema(description = "관광지 ID")
    private int contentId;

    // 조인을 위해 관광지 정보도 일부 포함
    @Schema(description = "관광지 이름 (조회용)", hidden = true)
    private String contentTitle;

    @Schema(description = "후기/메모")
    private String description;

    @Schema(description = "비용")
    private int cost;

    @Schema(description = "방문 순서")
    private int visitOrder;

    @Schema(description = "인증샷 URL")
    private String imageUrl;
}