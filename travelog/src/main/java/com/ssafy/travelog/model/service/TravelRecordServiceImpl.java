package com.ssafy.travelog.model.service;

import com.ssafy.travelog.model.dto.TravelDetailDto;
import com.ssafy.travelog.model.dto.TravelRecordDto;
import com.ssafy.travelog.model.mapper.TravelDetailMapper; // [import 확인]
import com.ssafy.travelog.model.mapper.TravelRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // [import 확인]

import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelRecordServiceImpl implements TravelRecordService {

    private final TravelRecordMapper recordMapper;

    // [중요] 상세 일정 Mapper를 주입받아야 DB에 저장할 수 있습니다.
    private final TravelDetailMapper detailMapper;

    @Override
    public void writeRecord(TravelRecordDto dto) {
        recordMapper.insertRecord(dto);
    }

    @Override
    public List<TravelRecordDto> getPublicRecords() {
        return recordMapper.selectAll();
    }

    @Override
    public List<TravelRecordDto> getMyRecords(String email) {
        return recordMapper.selectMyRecords(email);
    }

    // [추가] 상세 일정 저장 (트랜잭션 처리)
    @Override
    @Transactional // 여러 개를 저장하다가 하나라도 실패하면 전체 취소(롤백)
    public void addDetails(int recordId, List<TravelDetailDto> details) {
        for (TravelDetailDto detail : details) {
            detail.setTravelRecordId(recordId); // 부모 여행 기록 ID 연결
            detailMapper.insertDetail(detail);  // 하나씩 DB에 저장
        }
    }

    // [추가] 상세 일정 조회
    @Override
    public List<TravelDetailDto> getDetails(int recordId) {
        return detailMapper.selectDetailsByRecordId(recordId);
    }

    // [추가] 여행 기록 삭제
    @Override
    public void deleteRecord(int id) {
        recordMapper.deleteRecord(id);
    }

    // [추가] 상세 일정 삭제
    @Override
    public void deleteDetail(int detailId) {
        detailMapper.deleteDetail(detailId);
    }
}