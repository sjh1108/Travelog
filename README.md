# ✈️ Travelog (트래블로그)
> **여행의 순간을 기록하고, 취향에 맞는 여행지를 추천받는 소셜 웹 플랫폼**

## 📅 프로젝트 개요
* **개발 기간**: 2025.11.20 ~ (총 6주 / 핵심 기능 구현 2주)
* **팀 구성**: 2인 팀 프로젝트
* **주요 기능**: 여행 기록(Log), 소셜 피드(SNS), 개인화 여행지 추천

---

## 🛠️ Tech Stack

### Backend
<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white"> <img src="https://img.shields.io/badge/spring boot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/spring security-%236DB33F.svg?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/MyBatis-%23000000.svg?style=for-the-badge&logo=mybatis&logoColor=white">
* **Language**: Java 17
* **Framework**: Spring Boot 3.3.x
* **Build Tool**: Maven
* **Database**: MySQL 8.0
* **Auth**: JWT (Json Web Token)
* **API Docs**: SpringDoc (Swagger)
* **Infra**: AWS S3 (Image Storage)

### Frontend
<img src="https://img.shields.io/badge/vue.js-%234FC08D.svg?style=for-the-badge&logo=vuedotjs&logoColor=white"> <img src="https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=F7DF1E">
* **Framework**: Vue.js 3
* **Map API**: Kakao Maps API

### Collaboration
<img src="https://img.shields.io/badge/git-%23F05032.svg?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white">

---

## 📄 API Specification
API 명세서는 노션(Notion)을 통해 관리하고 있으며, Swagger를 통해 실시간 테스트가 가능합니다.

* **[👉 API 명세서 (Notion Link) 바로가기](https://www.notion.so/API-2b293e7eb35481659a4efc7c40de691c)**
* **Swagger UI**: `http://localhost:8080/swagger-ui/index.html` (서버 실행 시)

---

## 🗺️ Implementation Roadmap (2 Weeks)

효율적인 개발을 위해 기능 간 의존성을 고려하여 순차적으로 구현합니다.

### **Phase 1: 환경 설정 및 인증 (D-Day ~ D+2)**
- [x] 프로젝트 생성 및 Git 레포지토리 초기화
- [x] DB 설계 (ERD) 및 스키마 생성
- [ ] Spring Security + JWT 기반 회원가입/로그인 구현
- [ ] Vue.js 기본 레이아웃 구성

### **Phase 2: 공공 데이터 및 지도 연동 (D+3 ~ D+6)**
- [ ] 공공 데이터 API 연동 및 관광지 데이터 DB Batch 저장
- [ ] Kakao Map API 연동 및 지도 출력
- [ ] 지역별/테마별 관광지 조회 및 필터링 기능

### **Phase 3: 핵심 기능 - 여행 기록 & SNS (D+7 ~ D+10)**
- [ ] 여행 경로 그리기 및 사진 업로드 (AWS S3)
- [ ] 나만의 여행 로그(게시글) 작성/수정/삭제
- [ ] 타 유저 팔로우/팔로잉 및 뉴스피드 조회
- [ ] 게시글 좋아요 및 댓글 기능

### **Phase 4: 심화 기능 및 고도화 (D+11 ~ D+12)**
- [ ] 사용자 활동 기반 여행지 추천 알고리즘 구현 (Rule-based)
- [ ] 마이페이지 대시보드 (내 여행 통계 시각화)
- [ ] 통합 검색 기능

### **Phase 5: 테스트 및 배포 (D+13 ~ D+14)**
- [ ] 전체 기능 통합 테스트 및 버그 수정
- [ ] 최종 배포 및 시연 준비

---

## 📂 Directory Structure (Backend)

```text
src/main/java/com/ssafy/travelog
│
├── config          # Security, Swagger, WebMvc 설정
├── controller      # REST API Controller
├── model           # Domain Layer
│   ├── dto         # Data Transfer Object
│   ├── mapper      # MyBatis Mapper Interface
│   └── service     # Business Logic
└── util            # JWT, File Upload Utils
```

## 12월 5일 진행사항
1. 뷰 초안 제작
* 메인페이지
[메인 페이지](./image/Dec_5th/main_page.png)
* 게시글 페이지
[게시글 페이지](./image/Dec_5th/post_page.png)
[게시글 사진](./image/Dec_5th/post.png)
* 피드 페이지
[피드 페이지](./image/Dec_5th/feed.png)
* 마이 페이지
[마이 페이지](./image/Dec_5th/my_page.png)
* 업로드 페이지
[업로드 페이지](./image/Dec_5th/upload_page.png)