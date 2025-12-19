package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.TravelDetailDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TravelDetailMapper {
    // 상세 일정 1개 저장
    void insertDetail(TravelDetailDto detailDto);

    // 특정 여행 기록의 모든 상세 일정 조회 (순서대로)
    List<TravelDetailDto> selectDetailsByRecordId(int recordId);
}