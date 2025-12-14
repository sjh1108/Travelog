package com.ssafy.travelog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.sql.Date; // 날짜 타입

@Data
@Schema(description = "여행 기록(게시글) DTO")
public class TravelLogDto {
    @Schema(description = "로그 ID (PK)", hidden = true)
    private int logId;

    @Schema(description = "작성자 ID", hidden = true)
    private int userId;

    @Schema(description = "작성자 닉네임 (조회용)", hidden = true)
    private String userNickname;

    @Schema(description = "여행 제목", example = "2박 3일 부산 힐링 여행")
    private String title;

    @Schema(description = "여행 테마 (#힐링, #먹방)", example = "힐링")
    private String theme;

    @Schema(description = "시작 날짜")
    private Date startDate;

    @Schema(description = "종료 날짜")
    private Date endDate;

    @Schema(description = "생성 일시", hidden = true)
    private String createdAt;
}