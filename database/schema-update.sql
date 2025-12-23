-- ===============================================
-- 여행 기록 기능 개선을 위한 데이터베이스 스키마 업데이트
-- ===============================================

USE travelog;

-- 1. Travel_Records 테이블에 image_urls 컬럼 추가
ALTER TABLE Travel_Records
ADD COLUMN image_urls TEXT NULL COMMENT '여행 사진 URL 목록 (JSON 배열)';

-- 2. Travel_Details 테이블에 location_name 컬럼 추가
ALTER TABLE Travel_Details
ADD COLUMN location_name VARCHAR(255) NULL COMMENT '장소 이름';

-- 3. Travel_Details 테이블에 description 컬럼 추가
ALTER TABLE Travel_Details
ADD COLUMN description TEXT NULL COMMENT '메모/설명';

-- 4. Travel_Details 테이블에 image_urls 컬럼 추가
ALTER TABLE Travel_Details
ADD COLUMN image_urls TEXT NULL COMMENT '사진 URL 목록 (JSON 배열)';


-- 여행기록 좌표 테이블에 추가
  ALTER TABLE Travel_Records
  ADD COLUMN address VARCHAR(500) NULL,
  ADD COLUMN latitude DOUBLE NULL,
  ADD COLUMN longitude DOUBLE NULL;

-- ===============================================
-- 데이터 마이그레이션 (선택사항)
-- ===============================================

-- 기존 photos 컬럼의 데이터를 image_urls로 복사 (필요시)
-- UPDATE Travel_Details
-- SET image_urls = photos
-- WHERE photos IS NOT NULL;

-- ===============================================
-- 확인 쿼리
-- ===============================================

-- Travel_Records 테이블 구조 확인
DESCRIBE Travel_Records;

-- Travel_Details 테이블 구조 확인
DESCRIBE Travel_Details;

-- ===============================================
-- 샘플 데이터 (테스트용)
-- ===============================================

/*
-- Travel_Records 샘플
INSERT INTO Travel_Records
  (user_id, title, start_date, end_date, total_cost, theme, is_public, image_urls)
VALUES
  (1, '서울 여행', '2024-01-01', '2024-01-03', 500000, '먹방', true,
   '["https://example.com/img1.jpg", "https://example.com/img2.jpg"]');

-- Travel_Details 샘플
INSERT INTO Travel_Details
  (travel_record_id, sequence_order, location_name, description, latitude, longitude, image_urls)
VALUES
  (1, 1, '경복궁', '아름다운 궁궐이었습니다.', 37.5796, 126.9770,
   '["https://example.com/gyeongbok1.jpg", "https://example.com/gyeongbok2.jpg"]');
*/

-- ===============================================
-- 참고사항
-- ===============================================
-- 1. image_urls와 photos는 JSON 배열 형식의 TEXT로 저장됩니다
--    예: ["url1", "url2", "url3"]
--
-- 2. MyBatis TypeHandler (JsonListTypeHandler)가
--    Java List<String> <-> MySQL TEXT(JSON) 변환을 자동으로 처리합니다
--
-- 3. 기존 photos 컬럼은 하위 호환성을 위해 유지됩니다
--    필요시 데이터 마이그레이션 후 삭제 가능합니다
-- ===============================================
