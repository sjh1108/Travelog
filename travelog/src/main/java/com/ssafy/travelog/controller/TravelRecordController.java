package com.ssafy.travelog.controller;

import com.ssafy.travelog.model.dto.TravelDetailDto;
import com.ssafy.travelog.model.dto.TravelRecordDto;
import com.ssafy.travelog.model.service.TravelRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travels")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Travel Record 컨트롤러", description = "여행 기록 관리")
public class TravelRecordController {

    private final TravelRecordService travelService;

    @Operation(summary = "여행 기록 작성")
    @PostMapping
    public ResponseEntity<?> write(@RequestBody TravelRecordDto dto, Authentication authentication) {
        try {
            String email = (String) authentication.getPrincipal();
            dto.setUserEmail(email); // 작성자 설정

            log.info("여행 기록 작성 요청: {}", dto);
            travelService.writeRecord(dto);
            return new ResponseEntity<>("여행 기록 저장 성공", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("여행 기록 작성 실패", e);
            return new ResponseEntity<>("저장 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "전체 여행 기록 조회 (공개만)")
    @GetMapping
    public ResponseEntity<?> list() {
        return new ResponseEntity<>(travelService.getPublicRecords(), HttpStatus.OK);
    }

    @Operation(summary = "내 여행 기록 조회")
    @GetMapping("/my")
    public ResponseEntity<?> mylist(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        return new ResponseEntity<>(travelService.getMyRecords(email), HttpStatus.OK);
    }

    @Operation(summary = "상세 일정 추가 (단일)")
    @PostMapping("/{recordId}/details")
    public ResponseEntity<?> addDetail(@PathVariable int recordId, @RequestBody TravelDetailDto detail) {
        try {
            log.info("상세 일정 추가 요청: Record ID={}, Location={}", recordId, detail.getLocationName());
            // 단일 객체를 List로 변환
            List<TravelDetailDto> details = List.of(detail);
            travelService.addDetails(recordId, details);
            return new ResponseEntity<>("상세 일정 저장 성공", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("상세 일정 저장 실패", e);
            return new ResponseEntity<>("저장 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "상세 일정 추가 (여러 개)")
    @PostMapping("/{recordId}/details/batch")
    public ResponseEntity<?> addDetails(@PathVariable int recordId, @RequestBody List<TravelDetailDto> details) {
        try {
            log.info("상세 일정 추가 요청: Record ID={}, 개수={}", recordId, details.size());
            travelService.addDetails(recordId, details);
            return new ResponseEntity<>("상세 일정 저장 성공", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("상세 일정 저장 실패", e);
            return new ResponseEntity<>("저장 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "상세 일정 조회")
    @GetMapping("/{recordId}/details")
    public ResponseEntity<?> getDetails(@PathVariable int recordId) {
        return new ResponseEntity<>(travelService.getDetails(recordId), HttpStatus.OK);
    }
}