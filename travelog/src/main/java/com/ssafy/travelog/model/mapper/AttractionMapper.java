package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.AttractionDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AttractionMapper {
    // 관광지 정보 저장 (공공 API -> DB)
    void insertAttraction(AttractionDto attractionDto);

    // 관광지 검색 (지역별, 검색어별)
    List<AttractionDto> searchAttractions(String keyword);

    // ID로 관광지 하나 조회
    AttractionDto selectAttractionById(int contentId);
}   