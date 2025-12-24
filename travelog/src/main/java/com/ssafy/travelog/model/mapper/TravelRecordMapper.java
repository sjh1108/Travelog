package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.TravelRecordDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TravelRecordMapper {
    // 여행 기록 저장
    void insertRecord(TravelRecordDto travelRecordDto);

    // 전체 목록 조회 (공개된 것만)
    List<TravelRecordDto> selectAll();

    // 내 여행 기록 조회
    List<TravelRecordDto> selectMyRecords(String email);

    // 여행 기록 삭제
    int deleteRecord(int id);
}