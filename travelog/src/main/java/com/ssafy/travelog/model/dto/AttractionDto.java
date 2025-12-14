package com.ssafy.travelog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "관광지 정보 DTO")
public class AttractionDto {
    @Schema(description = "관광지 ID (공공 API ID)")
    private int contentId;

    @Schema(description = "관광지명")
    private String title;

    @Schema(description = "주소")
    private String addr1;

    @Schema(description = "이미지 URL")
    private String firstImage;

    @Schema(description = "경도 (X좌표)")
    private double mapX;

    @Schema(description = "위도 (Y좌표)")
    private double mapY;

    @Schema(description = "관광 타입 (12:관광지, 39:식당 등)")
    private int contentTypeId;
}