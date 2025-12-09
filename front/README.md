# TravelSnap - Vue 3 Travel Photo SNS

Next.js + React에서 Vue 3 + Vite로 변환된 여행 사진 공유 SNS 애플리케이션입니다.

## 기술 스택

- **Vue 3** - Composition API와 `<script setup>` 사용
- **TypeScript** - 타입 안전성
- **Vite** - 빠른 개발 서버
- **Vue Router** - 라우팅
- **Pinia** - 상태 관리 (Zustand에서 변환)
- **Tailwind CSS** - 스타일링
- **lucide-vue-next** - 아이콘

## 주요 기능

- 📸 여행 사진 업로드 및 공유
- 🗺️ 여행 지도 (Map 통합 가능)
- 💬 댓글 및 좋아요 기능
- 👤 사용자 프로필
- 🔍 무한 스크롤 피드

## 프로젝트 구조

```
vue-travel-sns/
├── src/
│   ├── assets/          # 정적 파일 (CSS)
│   ├── components/      # Vue 컴포넌트
│   ├── data/           # 더미 데이터
│   ├── router/         # Vue Router 설정
│   ├── stores/         # Pinia 스토어
│   ├── types/          # TypeScript 타입 정의
│   ├── views/          # 페이지 컴포넌트
│   ├── lib/            # 유틸리티 함수
│   ├── App.vue         # 루트 컴포넌트
│   └── main.ts         # 진입점
├── public/             # 정적 리소스
├── index.html
├── vite.config.ts
├── tailwind.config.js
└── package.json
```

## 설치 및 실행

```bash
# 의존성 설치
npm install

# 개발 서버 실행
npm run dev

# 프로덕션 빌드
npm run build

# 빌드 미리보기
npm run preview
```

## 주요 변환 사항

### React → Vue 변환

1. **상태 관리**: Zustand → Pinia
2. **라우팅**: Next.js App Router → Vue Router
3. **컴포넌트**: JSX/TSX → Vue SFC (Single File Components)
4. **Hook**: React Hooks → Vue Composition API
5. **이미지**: Next.js Image → 일반 img 태그

### 컴포넌트 구조

- `Navigation.vue` - 상단 네비게이션 바
- `Footer.vue` - 하단 푸터
- `PostCard.vue` - 게시물 카드
- `LikeButton.vue` - 좋아요 버튼
- `CommentModal.vue` - 댓글 모달

### 페이지 (Views)

- `FeedView.vue` - 메인 피드 (무한 스크롤)
- `UploadView.vue` - 사진 업로드
- `ProfileView.vue` - 사용자 프로필
- `PostDetailView.vue` - 게시물 상세
- `MapView.vue` - 여행 지도
- `MyPageView.vue` - 마이페이지

## 추가 구현 필요 사항

- [ ] Kakao Map API 통합
- [ ] 실제 백엔드 API 연동
- [ ] 사용자 인증 시스템
- [ ] 이미지 업로드 서버
- [ ] 반응형 디자인 개선
- [ ] 다크 모드 토글

## 라이센스

MIT
