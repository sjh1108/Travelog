package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.LogDetailDto;
import com.ssafy.travelog.model.dto.TravelLogDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TravelLogMapper {
    // 1. 여행 기록 작성 (메인)
    void insertLog(TravelLogDto travelLogDto);

    // 2. 여행 상세 경로 작성 (다중 Insert)
    void insertLogDetail(LogDetailDto logDetailDto);

    // 3. 전체 여행 기록 조회 (SNS 피드용)
    List<TravelLogDto> selectAllLogs();

    // 4. 특정 여행 기록 상세 조회 (로그 정보만)
    TravelLogDto selectLogById(int logId);

    // 5. 특정 여행 기록의 상세 경로들 조회 (지도 표시용)
    List<LogDetailDto> selectDetailsByLogId(int logId);
}