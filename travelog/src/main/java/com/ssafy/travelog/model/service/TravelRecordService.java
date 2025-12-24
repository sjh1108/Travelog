package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.TravelDetailDto;
import com.ssafy.travelog.model.dto.TravelRecordDto;
import java.util.List;

public interface TravelRecordService {
    void writeRecord(TravelRecordDto dto);
    List<TravelRecordDto> getPublicRecords();
    List<TravelRecordDto> getMyRecords(String email);
    void addDetails(int recordId, List<TravelDetailDto> details);
    List<TravelDetailDto> getDetails(int recordId);
    void deleteRecord(int id);
    void deleteDetail(int detailId);
}