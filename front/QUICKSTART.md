# 🚀 빠른 시작 가이드

## ⚡ 3단계로 시작하기

```bash
# 1️⃣ 의존성 설치
npm install

# 2️⃣ 개발 서버 실행
npm run dev

# 3️⃣ 브라우저에서 열기
# http://localhost:5173
```

## 🎯 즉시 사용 가능한 기능

✅ **피드 보기** - `/feed` 경로에서 게시물 확인  
✅ **사진 업로드** - `/upload`에서 드래그 앤 드롭으로 사진 올리기  
✅ **프로필 보기** - 사용자 클릭하면 프로필 페이지로 이동  
✅ **좋아요/댓글** - 실시간으로 작동하는 인터랙션  
✅ **무한 스크롤** - 피드를 스크롤하면 자동으로 더 로드  

## 🧪 테스트 시나리오

1. **메인 피드 확인**
   - 페이지 접속 시 자동으로 `/feed`로 리다이렉트
   - 3개의 샘플 게시물 표시
   - 스크롤 시 추가 게시물 로드

2. **사진 업로드**
   - 상단 메뉴에서 "Upload" 클릭
   - 이미지 파일 드래그 앤 드롭
   - 위치와 캡션 입력 후 "Share Photo" 버튼
   - 피드로 돌아가서 새 게시물 확인

3. **좋아요 기능**
   - 게시물의 하트 아이콘 클릭
   - 애니메이션 효과와 함께 좋아요 수 증가
   - 다시 클릭하면 취소

4. **댓글 작성**
   - 게시물의 말풍선 아이콘 클릭
   - 모달에서 댓글 입력
   - 실시간으로 댓글 목록에 추가

5. **프로필 보기**
   - 게시물의 사용자 이름 클릭
   - 프로필 페이지에서 해당 사용자의 모든 게시물 확인
   - "Follow" 버튼으로 팔로우/언팔로우

6. **게시물 상세**
   - 게시물 이미지 클릭
   - 큰 화면에서 상세 정보와 댓글 확인

## 📦 빌드하기

```bash
# 프로덕션 빌드
npm run build

# 빌드 결과물 미리보기
npm run preview
```

빌드된 파일은 `dist/` 폴더에 생성됩니다.

## 🛠️ 개발 도구

### VSCode 추천 확장

- Vue - Official
- TypeScript Vue Plugin (Volar)
- Tailwind CSS IntelliSense
- ESLint

### 유용한 명령어

```bash
# 타입 체크
npm run build  # 빌드 과정에서 타입 체크 수행

# 린트
npm run lint   # 코드 스타일 검사
```

## 🎨 커스터마이징

### 색상 변경

`src/assets/styles.css` 파일에서 CSS 변수 수정:

```css
:root {
  --primary: 0 0% 9%;           /* 메인 컬러 */
  --secondary: 0 84.2% 60.2%;   /* 보조 컬러 */
  --accent: 0 0% 96.1%;         /* 강조 컬러 */
}
```

### 더미 데이터 수정

`src/data/dummy-data.ts` 파일에서 샘플 데이터 변경

## 🐛 문제 해결

### 포트가 이미 사용 중인 경우

```bash
# 다른 포트로 실행
npm run dev -- --port 3000
```

### 의존성 설치 오류

```bash
# 캐시 삭제 후 재설치
rm -rf node_modules package-lock.json
npm install
```

### TypeScript 오류

```bash
# VSCode에서 TypeScript 서버 재시작
Ctrl/Cmd + Shift + P → "TypeScript: Restart TS Server"
```

## 📚 더 알아보기

- 전체 가이드: `GUIDE.md` 참고
- Vue 3 문서: https://vuejs.org
- Vite 문서: https://vitejs.dev
- Pinia 문서: https://pinia.vuejs.org

---

**즐거운 개발 되세요!** 🎉
