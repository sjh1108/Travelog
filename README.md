# TraveLog
### [발표 자료](https://www.canva.com/design/DAG4p_07ZEY/QwqFHXmKrTUZ9WLMfyEEvg/edit?utm_content=DAG4p_07ZEY&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

# 1. 문제 정의
여행 가서 사진 많이 찍으시나요?
그런데 단순히 사진만 남아
추억을 되살리기 힘들었던 경험 있으신가요?

<br>

# 2. 페르소나
*  여행을 좋아하는 A
    친구들과 여행 이야기를 할 때마다 언제 어느 곳으로 여행을 했는지 매번 기억하기가 힘들다.
    여행을 하며 찍었던 사진을 찾고 관리하기가 힘들다.
    그 래 서 추억 정리도 편하고 여행지도 관리하고!!
    더 나아가서 팔로우 한 사람들의 여행지와 사진도 구경할 수 있는 여행 SNS가 필요할 것 같다.
* 다음 행선지를 정하고 싶은 B
    진짜 여행 분위기를 알고 싶은 탐험가 B는
    다른 사람들이 실제로 다녀온 여행 사진과 솔직한 후기를 보며 여행지를 선택하고 싶어 한다.
    광고나 과장된 정보보다 픙경이나 지역을 즐길 수 있는 스팟과 할 거리를 직관적으로 확인할 수 있는 진짜 여행 분위기를 중요하게 여긴다.
    또한 여행을 다녀온 후에는 자신이 찍은 사진을 지역·스팟별로 자동으로 정리해주는 기능을 원한다.
    전반적으로 실제 사용자 중심의 여행 정보와 사진 기반 탐색 방식을 선호한다.
<br>

# 3. 핵심 기능 정의

## 1. 회원 및 인증 기능 (필수)

서비스의 가장 기본이 되는 기능입니다.
* [cite_start]**회원 관리 (CRUD):** 회원가입, 내 정보 조회, 프로필 수정, 회원 탈퇴 기능을 구현합니다[cite: 24, 69, 209].
* [cite_start]**로그인/로그아웃:** 사용자가 시스템에 접근 인증을 받고, 안전하게 로그아웃하는 기능을 구현합니다[cite: 24, 70, 209]. [cite_start](필요시 '비밀번호 찾기' 포함 [cite: 24, 209])

---

## 2. 여행 정보 탐색 기능 (필수)

SNS 기록의 '배경 데이터'가 되는 필수 기능입니다.
* [cite_start]**공공 데이터 연동:** 한국관광공사 등의 API를 활용해 기본이 되는 지역별 관광지 정보를 수집합니다[cite: 23, 209].
* [cite_start]**정보 통합 검색:** 사용자가 메인 페이지 등에서 지역별, 컨텐츠별(관광지, 숙박, 음식점, 문화시설, 쇼핑 등)로 원하는 관광 정보를 조회할 수 있습니다[cite: 24, 68, 209].

---

## 3. 여행 기록(Log) 기능 (핵심)

사용자님의 SNS 아이디어를 구현하는 핵심 파트입니다. [cite_start](기획서의 'hotplace 등록' [cite: 24, 209] 기능을 '기록'으로 확장)
* **여행 기록 작성:** 사용자가 (2번에서 조회한) 특정 여행지에 대해 사진, 글, 여행 경비, 테마 태그 등을 포함한 '기록(Log)'을 작성하고 저장합니다.
* **내 기록 조회 (마이페이지):** 내가 작성한 모든 여행 기록을 모아보거나, '여행지 기준'으로 내 기록을 필터링하여 찾아볼 수 있습니다.
* **여행 기록 수정/삭제:** 본인이 작성한 기록을 수정하거나 삭제합니다.

---

## 4. 소셜(SNS) 기능 (핵심)

'Travelog'을 단순한 기록장이 아닌 SNS로 만드는 핵심 기능입니다.
* **여행지 기반 피드:** (2번 기능으로) 특정 여행지를 조회할 때, 해당 여행지에 대해 다른 사용자들이 작성한 '여행 기록' 목록을 함께 구경할 수 있습니다.
* **팔로우/팔로잉:** 다른 사용자의 기록이 마음에 들 경우, 해당 사용자를 팔로우/언팔로우할 수 있습니다.
* **소셜 피드 (홈):** 내가 팔로우한 사용자들이 작성한 최신 '여행 기록'을 시간순으로 모아볼 수 있습니다.

---

## 5. 여행 추천 기능 (확장)

사용자에게 추가적인 가치를 제공하는 기능입니다.
* **개인화 여행지 추천:** 사용자가 작성한 '여행 기록'의 데이터(방문지, 경비, 선호 테마 등)를 분석하여 다음 여행지를 추천합니다.

이처럼 **필수 기능(1, 2번)**을 기본 토대로 삼고, 그 위에 'Travelog'만의 **핵심 SNS 기능(3, 4번)**을 구축하며, **확장 기능(5번)**으로 서비스의 매력을 더하는 방향으로 정의할 수 있습니다.

이 정의를 바탕으로 다음 단계인 WBS(작업분업구조)나 간트 차트 설계를 진행해 볼까요?

<br>

# 4. 서비스 구조 & 사용자 흐름
### 🗺️ Travelog 서비스 주요 사용자 흐름

#### 1. 신규 사용자 가입 및 로그인 흐름
> **목표:** 사용자가 서비스에 가입하고 인증을 완료합니다.

* `시작 (랜딩 페이지)` → `[회원가입] 버튼 클릭`
* → `회원가입 폼 (정보 입력)` → `[가입 완료] 버튼 클릭`
* → `로그인 페이지로 이동` → `아이디/비밀번호 입력` → `[로그인] 버튼 클릭`
* → **`메인 페이지 (소셜 피드) 진입`**

#### 2. 여행지 탐색 및 기록 조회 흐름
> **목표:** 사용자가 특정 여행지의 정보와 다른 사람들의 기록을 탐색합니다.

* `메인 페이지 (로그인 상태)` → `[검색창] 또는 [탐색 탭] 클릭`
* [cite_start]→ `'부산' 등 특정 여행지 검색` [cite: 54, 211]
* → **`여행지 상세 페이지 ('부산')`**
    * [cite_start]*(표시 내용: 1. '부산' 관광 정보 (공공 API) [cite: 54, 211] / 2. '부산' 관련 여행 기록(Log) 목록)*
* → `목록에서 특정 '여행 기록' 클릭`
* → **`기록 상세 페이지 (A 유저의 부산 여행기)`**
    * *(표시 내용: 사진, 글, 경비, 테마 등)*
* → `작성자 [A 유저 프로필] 클릭`
* → **`A 유저 프로필 페이지`** → `[팔로우] 버튼 클릭`

#### 3. 여행 기록 작성 흐름
> **목표:** 사용자가 자신의 여행 경험을 시스템에 기록합니다.

* `메인 페이지 (로그인 상태)` → `[기록 작성] 버튼 클릭`
* → **`기록 작성 페이지 (에디터)`**
* → `여행지 검색/선택 (예: '부산')`
* → `사진 업로드, 글 작성, 경비/테마 태그 입력`
* → `[저장] 버튼 클릭`
* → **`내 페이지 (My Page)로 이동`** (방금 작성한 기록이 목록 최상단에 표시됨)

#### 4. 소셜 피드 이용 흐름
> **목표:** 사용자가 팔로우한 다른 이용자들의 소식을 확인합니다.

* `로그인` →  메인 페이지 (전국 지도) → **`소셜 페이지 `**
* → `(팔로우한 사용자들의 최신 기록이 나열됨)`
* → `피드 스크롤 및 탐색`
* → `관심 있는 '기록' 클릭` → **`기록 상세 페이지`**

---

이러한 사용자 흐름 정의는 다음 단계인 **'5. 와이어프레임 / UX'** 를 설계할 때 어떤 화면이 필요하고, 화면 간의 이동(내비게이션)을 어떻게 구성해야 할지 알려주는 기반이 됩니다.

바로 이어서 '5. 와이어프레임 / UX' 단계에 필요한 **주요 화면 목록**을 정의해 볼까요?

# 5. 와이어 프레임 / UX

# 6. 기술 스택

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
<br>

<img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> 
<img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white"> 
<img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"> 
<img src="https://img.shields.io/badge/vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white">
<br>

<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
<img src="https://img.shields.io/badge/MyBatis-000000?style=for-the-badge&logo=MyBatis&logoColor=white"> 
<br>

<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<br>

<img src="https://img.shields.io/badge/apachetomcat-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black"> 
<br>

<img src="https://img.shields.io/badge/-Spring%20Tool%20Suite%204-6DB33F?style=flat-square&logo=eclipse&logoColor=white">
<img src="https://img.shields.io/badge/visualstudiocode-007ACC?style=for-the-badge&logo=visualstudiocode&logoColor=white"> 
<img src="https://img.shields.io/badge/intellijidea-000000?style=for-the-badge&logo=intellijidea&logoColor=white"> 
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">

