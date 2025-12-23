<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-6xl mx-auto">
        <div class="flex justify-between items-center mb-8">
          <h1 class="text-3xl font-bold">
            {{ isMyMap ? "My Map" : "Travel Map" }}
          </h1>
          <div class="flex gap-2">
            <button
              v-if="store.isLoggedIn && isMyMap"
              @click="showTravelForm = true"
              class="px-4 py-2 bg-secondary text-secondary-foreground rounded-lg hover:bg-secondary/90 transition-colors"
            >
              + Create Travel
            </button>
            <button
              @click="toggleMapMode"
              class="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
            >
              {{ isMyMap ? "Travel Map" : "My Map" }}
            </button>
          </div>
        </div>
        <!-- 이 자리에 관광지 정보를 지역별 원하는 콘텐츠 조회할 수 있는 메뉴 -->
        <!--<div v-if="!isMyMap" class="ml-[45%] mb-4">
          <p>관광지 필터 메뉴자리</p>
        </div>-->
        <!-- 카카오맵 컨테이너 -->
        <div class="bg-card border border-border rounded-lg overflow-hidden">
          <div id="kakao-map" class="w-full h-[600px]"></div>
        </div>

        <!-- Travel Logs (최근 3개) -->
        <div class="mt-8">
          <h2 class="text-2xl font-bold mb-4">Recent Travel Logs</h2>
          <div
            v-if="recentTravelLogs.length === 0"
            class="text-center py-8 text-foreground/50"
          >
            No travel logs yet
          </div>
          <div
            v-else
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4"
          >
            <div
              v-for="log in recentTravelLogs"
              :key="log.id"
              class="bg-card border border-border rounded-lg overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
              @click="openTravelDetail(log)"
            >
              <!-- 대표 이미지 썸네일 -->
              <div class="relative w-full h-48 bg-muted">
                <img
                  :src="getFullImageUrl(log.imageUrls?.[0] || log.photos?.[0] || log.images?.[0])"
                  :alt="log.title || 'Travel photo'"
                  class="w-full h-full object-cover"
                  @error="$event.target.src = '/placeholder.svg'"
                />
                <!-- 사진 개수 표시 -->
                <div
                  v-if="(log.imageUrls?.length || log.photos?.length || log.images?.length || 0) > 1"
                  class="absolute bottom-2 right-2 bg-black/70 text-white text-xs px-2 py-1 rounded"
                >
                  +{{ (log.imageUrls?.length || log.photos?.length || log.images?.length || 0) - 1 }} more
                </div>
              </div>

              <!-- 내용 -->
              <div class="p-4">
                <h3 class="font-bold text-lg mb-2 line-clamp-1">
                  {{ log.title || log.locationName || "Untitled" }}
                </h3>
                <p class="text-sm text-foreground/60 mb-2">
                  {{ formatDate(log.startDate || log.date || log.visitDate) }}
                  <span v-if="log.endDate && log.endDate !== log.startDate">
                    ~ {{ formatDate(log.endDate) }}
                  </span>
                </p>
                <p class="text-sm text-foreground/80 line-clamp-2 mb-3">
                  {{ log.review || log.description || log.theme || "No description" }}
                </p>

                <!-- 상세보기 버튼 -->
                <button
                  @click.stop="openTravelDetail(log)"
                  class="text-primary text-sm font-medium hover:underline"
                >
                  View Details →
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    <Footer />

    <!-- 이미지 갤러리 모달 -->
    <div
      v-if="showImageModal"
      class="fixed inset-0 bg-black/80 z-50 flex items-center justify-center p-4"
      @click="closeModal"
    >
      <div
        class="bg-card rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto"
        @click.stop
      >
        <div class="p-6">
          <!-- 모달 헤더 -->
          <div class="flex justify-between items-start mb-4">
            <div>
              <h2 class="text-2xl font-bold mb-2">{{ selectedSpot?.title }}</h2>
              <p class="text-sm text-foreground/60">
                {{ selectedSpot?.address }}
              </p>
            </div>
            <button
              @click="closeModal"
              class="text-foreground/60 hover:text-foreground text-2xl"
            >
              ×
            </button>
          </div>

          <!-- 이미지가 없을 때 -->
          <div v-if="selectedSpotImages.length === 0" class="text-center py-8">
            <p class="text-foreground/60">이미지가 없습니다</p>
          </div>

          <!-- 메인 이미지 -->
          <div v-else class="mb-4">
            <div class="relative">
              <img
                :src="selectedSpotImages[currentImageIndex]?.originimgurl"
                :alt="`${selectedSpot?.title} - 이미지 ${
                  currentImageIndex + 1
                }`"
                class="w-full h-auto max-h-[60vh] object-contain rounded-lg cursor-pointer"
                @click="openFullImage"
              />

              <!-- 좌우 화살표 버튼 -->
              <button
                v-if="currentImageIndex > 0"
                @click="prevImage"
                class="absolute left-2 top-1/2 -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white rounded-full w-10 h-10 flex items-center justify-center"
              >
                ‹
              </button>
              <button
                v-if="currentImageIndex < selectedSpotImages.length - 1"
                @click="nextImage"
                class="absolute right-2 top-1/2 -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white rounded-full w-10 h-10 flex items-center justify-center"
              >
                ›
              </button>
            </div>

            <!-- 이미지 카운터 -->
            <div class="text-center mt-2 text-sm text-foreground/60">
              {{ currentImageIndex + 1 }} / {{ selectedSpotImages.length }}
            </div>
          </div>

          <!-- 썸네일 그리드 -->
          <div
            v-if="selectedSpotImages.length > 1"
            class="grid grid-cols-4 md:grid-cols-6 gap-2"
          >
            <div
              v-for="(image, index) in selectedSpotImages"
              :key="index"
              @click="currentImageIndex = index"
              :class="[
                'cursor-pointer rounded overflow-hidden border-2 transition-all',
                currentImageIndex === index
                  ? 'border-primary'
                  : 'border-transparent hover:border-foreground/30',
              ]"
            >
              <img
                :src="image.smallimageurl || image.originimgurl"
                :alt="`썸네일 ${index + 1}`"
                class="w-full h-20 object-cover"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 원본 이미지 전체화면 모달 -->
    <div
      v-if="showFullImage"
      class="fixed inset-0 bg-black/95 z-[60] flex items-center justify-center p-4"
      @click="showFullImage = false"
    >
      <button
        @click="showFullImage = false"
        class="absolute top-4 right-4 text-white text-3xl hover:text-gray-300"
      >
        ×
      </button>
      <img
        :src="selectedSpotImages[currentImageIndex]?.originimgurl"
        :alt="`${selectedSpot?.title} - 원본 이미지`"
        class="max-w-full max-h-full object-contain"
        @click.stop
      />
    </div>

    <!-- 여행 기록 작성 모달 -->
    <div
      v-if="showTravelForm"
      class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
      @click="showTravelForm = false"
    >
      <div
        class="bg-background rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto"
        @click.stop
      >
        <div class="p-6">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-2xl font-bold">여행 기록 작성</h2>
            <button
              @click="showTravelForm = false"
              class="text-foreground/60 hover:text-foreground text-2xl"
            >
              ×
            </button>
          </div>

          <form @submit.prevent="handleCreateTravel" class="space-y-4">
            <!-- 이미지 업로드 영역 -->
            <div>
              <label class="block text-sm font-medium mb-2">사진</label>
              <div
                class="border-2 border-dashed border-border rounded-lg p-6 text-center cursor-pointer hover:border-primary transition-colors"
                @click="triggerFileInput"
                @dragover.prevent
                @drop.prevent="handleFileDrop"
              >
                <p class="text-foreground/60 mb-2">
                  사진을 드래그하거나 클릭하여 선택하세요
                </p>
                <p class="text-xs text-foreground/40">PNG, JPG 형식 (여러 장 가능)</p>
                <input
                  ref="fileInput"
                  type="file"
                  accept="image/*"
                  multiple
                  class="hidden"
                  @change="handleFileSelect"
                />
              </div>

              <!-- 미리보기 그리드 -->
              <div v-if="previewUrls.length > 0" class="grid grid-cols-3 gap-2 mt-4">
                <div
                  v-for="(url, index) in previewUrls"
                  :key="index"
                  class="relative group"
                >
                  <img
                    :src="url"
                    alt="Preview"
                    class="w-full h-24 object-cover rounded-lg"
                  />
                  <button
                    type="button"
                    @click="removeImage(index)"
                    class="absolute top-1 right-1 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    ×
                  </button>
                </div>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium mb-2">제목 *</label>
              <input
                v-model="travelForm.title"
                type="text"
                required
                placeholder="여행 제목을 입력하세요"
                class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
              />
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium mb-2"
                  >시작일 *</label
                >
                <input
                  v-model="travelForm.startDate"
                  type="date"
                  required
                  class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
                />
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">종료일 *</label>
                <input
                  v-model="travelForm.endDate"
                  type="date"
                  required
                  class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium mb-2">총 비용</label>
              <input
                v-model.number="travelForm.totalCost"
                type="number"
                placeholder="0"
                class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
              />
            </div>

            <div>
              <label class="block text-sm font-medium mb-2">테마</label>
              <input
                v-model="travelForm.theme"
                type="text"
                placeholder="예: 먹방, 관광, 휴양"
                class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
              />
            </div>

            <div class="flex items-center gap-2">
              <input
                v-model="travelForm.isPublic"
                type="checkbox"
                id="isPublic"
                class="w-4 h-4"
              />
              <label for="isPublic" class="text-sm font-medium"
                >여행 기록 공개</label
              >
            </div>

            <div class="flex gap-2 justify-end">
              <button
                type="button"
                @click="showTravelForm = false"
                class="px-4 py-2 bg-background border border-border rounded-lg hover:bg-foreground/5 transition-colors"
              >
                취소
              </button>
              <button
                type="submit"
                :disabled="isSubmitting"
                class="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors disabled:opacity-50"
              >
                {{ isSubmitting ? "작성 중..." : "작성하기" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 여행 상세 모달 -->
    <TravelDetailModal
      :is-open="showTravelDetailModal"
      :travel-id="selectedTravelId"
      :on-close="closeTravelDetail"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { MapPin } from "lucide-vue-next";
import axios from "axios";
import Navigation from "@/components/Navigation.vue";
import Footer from "@/components/Footer.vue";
import TravelDetailModal from "@/components/TravelDetailModal.vue";
import { useAppStore } from "@/stores/app";
import { travelAPI, fileAPI } from "@/api/api";
import { getFullImageUrl } from "@/utils/imageUtils";

// ==================== State Variables ====================
const store = useAppStore();

// 맵 모드 토글
const isMyMap = ref(false); // false: Travel Map, true: My Map

// 카카오맵 관련 상태
const map = ref(null); // 카카오맵 인스턴스
const markers = ref([]); // 지도에 표시된 마커 배열
const infoWindow = ref(null); // 마커 hover 시 표시되는 정보창

// 관광지 데이터
const touristSpots = ref([]); // 조회된 관광지 목록

// 여행 기록 데이터
const myTravelLogs = ref([]); // 내 여행 기록
const allTravelLogs = ref([]); // 전체 공개 여행 기록

// 이미지 갤러리 모달 상태
const selectedSpot = ref(null); // 선택된 관광지 정보
const selectedSpotImages = ref([]); // 선택된 관광지의 이미지 배열
const showImageModal = ref(false); // 이미지 갤러리 모달 표시 여부
const currentImageIndex = ref(0); // 현재 표시 중인 이미지 인덱스
const showFullImage = ref(false); // 원본 이미지 전체화면 표시 여부

// 여행 기록 작성 폼 상태
const showTravelForm = ref(false);
const isSubmitting = ref(false);
const travelForm = ref({
  title: "",
  startDate: "",
  endDate: "",
  totalCost: 0,
  theme: "",
  isPublic: true,
});
const selectedFiles = ref([]); // 선택된 파일들
const previewUrls = ref([]); // 미리보기 URL들
const fileInput = ref(null);

// 상세 모달 상태
const selectedTravelId = ref(null);
const showTravelDetailModal = ref(false);

// ==================== Computed & Utility Functions ====================
// 현재 지도 모드에 따라 표시할 여행 로그
const travelLogs = computed(() => {
  if (isMyMap.value) {
    return myTravelLogs.value;
  } else {
    return allTravelLogs.value;
  }
});

// 최근 3개의 여행 로그만 가져오기 (로그인 시 항상 내 기록 표시)
const recentTravelLogs = computed(() => {
  // 로그인한 경우 내 여행 기록 표시
  const logsToShow = store.isLoggedIn ? myTravelLogs.value : allTravelLogs.value;

  if (!logsToShow || logsToShow.length === 0) return [];

  // 날짜를 기준으로 정렬 (최신순)
  const sorted = [...logsToShow].sort((a, b) => {
    const dateA = new Date(a.startDate || a.date || a.visitDate || 0);
    const dateB = new Date(b.startDate || b.date || b.visitDate || 0);
    return dateB - dateA;
  });

  // 최근 3개만 반환
  return sorted.slice(0, 3);
});

// 여행 로그 불러오기 (내 여행 기록)
const fetchMyTravelLogs = async () => {
  try {
    const data = await travelAPI.getMyTravels();
    myTravelLogs.value = data;
  } catch (error) {
    console.error("내 여행 기록 조회 실패:", error);
  }
};

// 전체 여행 기록 불러오기 (모든 공개 여행 기록)
const fetchAllTravelLogs = async () => {
  try {
    const data = await travelAPI.getTravels();
    allTravelLogs.value = data;
  } catch (error) {
    console.error("전체 여행 기록 조회 실패:", error);
  }
};

// 날짜 포맷팅 (예: January 1, 2024)
const formatDate = (dateString) => {
  if (!dateString) return "Date not available";

  const date = new Date(dateString);

  // Invalid Date 체크
  if (isNaN(date.getTime())) {
    return "Invalid date";
  }

  return date.toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
};

// ==================== Kakao Map Functions ====================
// 카카오맵 SDK 스크립트를 동적으로 로드
const loadKakaoMapScript = () => {
  return new Promise((resolve, reject) => {
    // 이미 로드되어 있는지 확인
    if (window.kakao && window.kakao.maps) {
      resolve();
      return;
    }

    // 스크립트 태그 생성 및 삽입
    const script = document.createElement("script");
    const apiKey = import.meta.env.VITE_KAKAO_MAP_API_KEY;

    if (!apiKey) {
      console.error(
        "카카오맵 API 키가 설정되지 않았습니다. .env.local 파일을 확인해주세요."
      );
      reject(new Error("API key not found"));
      return;
    }

    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${apiKey}&autoload=false`;
    script.onload = () => resolve();
    script.onerror = () => reject(new Error("Failed to load Kakao Map script"));
    document.head.appendChild(script);
  });
};

// 카카오맵 초기화 및 관광지 데이터 로드
const initializeMap = async () => {
  window.kakao.maps.load(async () => {
    const container = document.getElementById("kakao-map");
    const options = {
      center: new window.kakao.maps.LatLng(36.3504, 127.3845), // 대전 중심
      level: 13, // 전국 지도가 보이는 확대 레벨
    };
    map.value = new window.kakao.maps.Map(container, options);

    // 초기 관광지 목록 조회 및 마커 표시
    const spots = await fetchTouristSpots();
    touristSpots.value = spots;
    updateMapMarkers(spots);
  });
};

// ==================== API Functions ====================
// 관광지 상세 이미지 조회 (한국관광공사 API - detailImage2)
const fetchTouristSpotImages = async (contentId) => {
  const apiKey = import.meta.env.VITE_TOUR_API_KEY;

  if (!apiKey || apiKey === "YOUR_TOUR_API_KEY") {
    console.error("공공데이터 API 키를 .env.local에 설정해주세요.");
    return [];
  }

  const params = {
    numOfRows: 10,
    pageNo: 1,
    MobileOS: "ETC",
    MobileApp: "Travelog",
    _type: "json",
    contentId: contentId,
    imageYN: "Y",
  };

  const queryString = new URLSearchParams(params).toString();
  const url = `/api/tour/detailImage2?serviceKey=${apiKey}&${queryString}`;

  try {
    const response = await axios.get(url);
    const data = response.data;

    if (data.response?.body?.items?.item) {
      const items = Array.isArray(data.response.body.items.item)
        ? data.response.body.items.item
        : [data.response.body.items.item];

      return items.map((item) => ({
        originimgurl: item.originimgurl, // 원본 이미지 URL
        smallimageurl: item.smallimageurl, // 썸네일 이미지 URL
      }));
    }
    return [];
  } catch (error) {
    console.error("관광지 이미지 조회 실패:", error);
    return [];
  }
};

// 관광지 목록 조회 (한국관광공사 API - areaBasedList2)
const fetchTouristSpots = async (queryParams = {}) => {
  const apiKey = import.meta.env.VITE_TOUR_API_KEY;

  if (!apiKey || apiKey === "YOUR_TOUR_API_KEY") {
    console.error("공공데이터 API 키를 .env.local에 설정해주세요.");
    return [];
  }

  const defaultParams = {
    numOfRows: 20, // 조회할 관광지 개수
    pageNo: 1,
    MobileOS: "ETC",
    MobileApp: "Travelog",
    _type: "json",
    arrange: "A", // 정렬 (A: 제목순)
  };

  const params = { ...defaultParams, ...queryParams };
  const queryString = new URLSearchParams(params).toString();
  const url = `/api/tour/areaBasedList2?serviceKey=${apiKey}&${queryString}`;

  try {
    const response = await axios.get(url);
    const data = response.data;

    if (data.response?.body?.items?.item) {
      const items = Array.isArray(data.response.body.items.item)
        ? data.response.body.items.item
        : [data.response.body.items.item];

      return items.map((item) => ({
        id: item.contentid, // 관광지 고유 ID
        title: item.title, // 관광지 이름
        address: item.addr1, // 주소
        image: item.firstimage || item.firstimage2, // 대표 이미지
        mapx: parseFloat(item.mapx), // 경도
        mapy: parseFloat(item.mapy), // 위도
        contentTypeId: item.contenttypeid, // 관광지 타입
      }));
    }
    return [];
  } catch (error) {
    console.error("관광지 데이터 조회 실패:", error);
    return [];
  }
};

// ==================== Map Marker Functions ====================
// 지도에 마커 추가 및 이벤트 등록
const updateMapMarkers = (spots) => {
  // 기존 마커 제거
  markers.value.forEach((marker) => marker.setMap(null));
  markers.value = [];

  // InfoWindow 인스턴스 생성 (hover 시 정보 표시용)
  if (!infoWindow.value) {
    infoWindow.value = new window.kakao.maps.InfoWindow({
      removable: false,
    });
  }

  // 각 관광지에 대해 마커 생성
  spots.forEach((spot) => {
    if (spot.mapx && spot.mapy) {
      const markerPosition = new window.kakao.maps.LatLng(spot.mapy, spot.mapx);

      const marker = new window.kakao.maps.Marker({
        position: markerPosition,
        title: spot.title,
      });

      marker.setMap(map.value);
      markers.value.push(marker);

      // 마우스 오버 시 간단한 정보창 표시
      window.kakao.maps.event.addListener(marker, "mouseover", () => {
        infoWindow.value.setContent(createInfoWindowContent(spot));
        infoWindow.value.open(map.value, marker);
      });

      // 마우스 아웃 시 정보창 숨김
      window.kakao.maps.event.addListener(marker, "mouseout", () => {
        infoWindow.value.close();
      });

      // 마커 클릭 시 이미지 갤러리 모달 열기
      window.kakao.maps.event.addListener(marker, "click", async () => {
        selectedSpot.value = spot;
        const images = await fetchTouristSpotImages(spot.id);
        selectedSpotImages.value = images;
        currentImageIndex.value = 0;
        showImageModal.value = true;
      });
    }
  });

  // 모든 마커가 보이도록 지도 범위 자동 조정
  if (spots.length > 0 && spots[0].mapx && spots[0].mapy) {
    const bounds = new window.kakao.maps.LatLngBounds();
    spots.forEach((spot) => {
      if (spot.mapx && spot.mapy) {
        bounds.extend(new window.kakao.maps.LatLng(spot.mapy, spot.mapx));
      }
    });
    map.value.setBounds(bounds);
  }
};

// 마커 hover 시 표시할 정보창 HTML 생성
const createInfoWindowContent = (spot) => {
  return `
    <div style="
      padding: 12px;
      min-width: 180px;
      max-width: 250px;
      box-sizing: border-box;
      font-family: sans-serif;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    ">
      <h3 style="
        margin: 0 0 8px 0;
        font-size: 14px;
        font-weight: bold;
        color: #333;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      ">${spot.title}</h3>
      ${
        spot.image
          ? `
        <img
          src="${spot.image}"
          style="
            width: 100%;
            height: auto;
            max-height: 120px;
            object-fit: cover;
            border-radius: 4px;
            margin-bottom: 8px;
          "
          onerror="this.style.display='none'"
        />
      `
          : ""
      }
      <p style="
        margin: 0;
        font-size: 12px;
        color: #666;
        line-height: 1.4;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      ">${spot.address || "주소 정보 없음"}</p>
    </div>
  `;
};

// ==================== Modal Functions ====================
// 모달 닫기
const closeModal = () => {
  showImageModal.value = false;
  showFullImage.value = false;
};

// 다음 이미지로 이동
const nextImage = () => {
  if (currentImageIndex.value < selectedSpotImages.value.length - 1) {
    currentImageIndex.value++;
  }
};

// 이전 이미지로 이동
const prevImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--;
  }
};

// 원본 이미지 전체화면으로 보기
const openFullImage = () => {
  showFullImage.value = true;
};

// ==================== Travel Record Functions ====================
// 파일 선택 트리거
const triggerFileInput = () => {
  fileInput.value?.click();
};

// 파일 선택 처리
const handleFileSelect = (event) => {
  const files = Array.from(event.target.files || []);
  addFiles(files);
};

// 드래그 앤 드롭 처리
const handleFileDrop = (event) => {
  const files = Array.from(event.dataTransfer?.files || []);
  const imageFiles = files.filter(file => file.type.startsWith('image/'));
  addFiles(imageFiles);
};

// 파일 추가 공통 로직
const addFiles = (files) => {
  files.forEach(file => {
    if (file.type.startsWith('image/')) {
      selectedFiles.value.push(file);
      previewUrls.value.push(URL.createObjectURL(file));
    }
  });
};

// 이미지 제거
const removeImage = (index) => {
  URL.revokeObjectURL(previewUrls.value[index]);
  selectedFiles.value.splice(index, 1);
  previewUrls.value.splice(index, 1);
};

// 여행 상세 모달 열기
const openTravelDetail = (log) => {
  selectedTravelId.value = log.id;
  showTravelDetailModal.value = true;
};

// 여행 상세 모달 닫기
const closeTravelDetail = () => {
  selectedTravelId.value = null;
  showTravelDetailModal.value = false;
};

// 여행 기록 작성
const handleCreateTravel = async () => {
  isSubmitting.value = true;

  try {
    let imageUrls = [];

    // 선택된 이미지가 있으면 업로드
    if (selectedFiles.value.length > 0) {
      try {
        console.log('이미지 업로드 시작...', selectedFiles.value.length, '개');
        const uploadResult = await fileAPI.uploadMultipleImages(selectedFiles.value);
        console.log('이미지 업로드 결과:', uploadResult);

        // 응답 형식에 따라 URL 배열 추출
        imageUrls = uploadResult.urls || uploadResult.data?.urls || uploadResult || [];

        // 문자열 배열이 아닌 경우 처리
        if (Array.isArray(imageUrls) && imageUrls.length > 0 && typeof imageUrls[0] === 'object') {
          imageUrls = imageUrls.map(img => img.url || img.imageUrl || img);
        }

        console.log('처리된 이미지 URLs:', imageUrls);
      } catch (uploadError) {
        console.warn('이미지 업로드 실패 (백엔드 미구현 가능성):', uploadError);
        // 이미지 업로드 실패 시에도 계속 진행할지 확인
        if (!confirm('이미지 업로드에 실패했습니다. 이미지 없이 여행 기록을 작성하시겠습니까?')) {
          isSubmitting.value = false;
          return;
        }
      }
    }

    const travelData = {
      ...travelForm.value,
      imageUrls: imageUrls
    };

    console.log('여행 기록 작성 데이터:', travelData);
    const newTravel = await travelAPI.createTravel(travelData);

    // 성공 후 폼 초기화
    travelForm.value = {
      title: "",
      startDate: "",
      endDate: "",
      totalCost: 0,
      theme: "",
      isPublic: true,
    };

    // 이미지 관련 상태 초기화
    selectedFiles.value.forEach((_, index) => {
      URL.revokeObjectURL(previewUrls.value[index]);
    });
    selectedFiles.value = [];
    previewUrls.value = [];

    // 모달 닫기
    showTravelForm.value = false;

    // 여행 기록 다시 불러오기
    await fetchMyTravelLogs();

    alert("Travel record created successfully!");
  } catch (error) {
    console.error("여행 기록 작성 실패:", error);
    alert("Failed to create travel record. Please try again.");
  } finally {
    isSubmitting.value = false;
  }
};

// ==================== Map Mode Toggle ====================
// 맵 모드 토글 함수
const toggleMapMode = async () => {
  // "My Map"으로 가려고 하는데, 로그인이 안되어있을 경우
  if (!isMyMap.value && !store.isLoggedIn) {
    store.showLoginModal = true;
    return; // 여기서 함수 실행을 중단
  }

  isMyMap.value = !isMyMap.value;

  // 모드에 따라 여행 기록 불러오기 및 마커 업데이트
  if (isMyMap.value) {
    // My Map: 내 여행 기록 불러오기
    // 이 시점에 도달했다면, My Map으로 전환하는 경우 반드시 로그인 되어 있음
    await fetchMyTravelLogs();
    updateMyMapMarkers();
  } else {
    // Travel Map: 전체 공개 여행 기록 불러오기
    await fetchAllTravelLogs();

    // 로그인이 되어있다면 내 여행 기록도 불러오기 (Recent Travel Logs 표시용)
    if (store.isLoggedIn) {
      await fetchMyTravelLogs();
    }

    updateMapMarkers(touristSpots.value);
  }
};

// My Map 마커 업데이트 (내 게시물 위치 표시)
const updateMyMapMarkers = () => {
  // 기존 마커 제거
  markers.value.forEach((marker) => marker.setMap(null));
  markers.value = [];

  // InfoWindow 인스턴스 생성
  if (!infoWindow.value) {
    infoWindow.value = new window.kakao.maps.InfoWindow({
      removable: false,
    });
  }

  // 위치 정보를 저장할 배열
  const positions = [];

  // 각 여행 로그에 대해 마커 생성
  travelLogs.value.forEach((log) => {
    const lat = log.latitude || 36.3504 + (Math.random() - 0.5) * 0.5;
    const lng = log.longitude || 127.3845 + (Math.random() - 0.5) * 0.5;

    positions.push({ lat, lng });

    const markerPosition = new window.kakao.maps.LatLng(lat, lng);
    const marker = new window.kakao.maps.Marker({
      position: markerPosition,
      title: log.locationName,
    });

    marker.setMap(map.value);
    markers.value.push(marker);

    // 마우스 오버 시 정보창 표시
    window.kakao.maps.event.addListener(marker, "mouseover", () => {
      infoWindow.value.setContent(createMyMapInfoWindowContent(log));
      infoWindow.value.open(map.value, marker);
    });

    // 마우스 아웃 시 정보창 숨김
    window.kakao.maps.event.addListener(marker, "mouseout", () => {
      infoWindow.value.close();
    });

    // 마커 클릭 시 이미지 갤러리 모달 열기
    window.kakao.maps.event.addListener(marker, "click", () => {
      selectedSpot.value = {
        title: log.title || log.locationName,
        address: log.description,
      };

      // travel log의 photos 또는 imageUrls를 이미지 형식에 맞게 변환
      const images = log.imageUrls || log.photos || [];
      selectedSpotImages.value = images.map((photo) => ({
        originimgurl: getFullImageUrl(photo),
        smallimageurl: getFullImageUrl(photo),
      }));

      currentImageIndex.value = 0;
      showImageModal.value = true;
    });
  });

  // 모든 마커가 보이도록 지도 범위 자동 조정
  if (positions.length > 0) {
    // 한국 내 위치인지 확인 (위도 33-43, 경도 124-132)
    const isKoreanLocation = positions.some(
      (pos) =>
        pos.lat >= 33 && pos.lat <= 43 && pos.lng >= 124 && pos.lng <= 132
    );

    if (isKoreanLocation) {
      // 한국 내 위치면 bounds 자동 조정
      const bounds = new window.kakao.maps.LatLngBounds();
      positions.forEach((pos) => {
        bounds.extend(new window.kakao.maps.LatLng(pos.lat, pos.lng));
      });
      map.value.setBounds(bounds);
    } else {
      // 해외 위치면 첫 번째 마커 중심으로 적절한 줌 레벨 설정
      const center = new window.kakao.maps.LatLng(
        positions[0].lat,
        positions[0].lng
      );
      map.value.setCenter(center);
      map.value.setLevel(8); // 적절한 줌 레벨
    }
  }
};

// My Map 정보창 HTML 생성
const createMyMapInfoWindowContent = (log) => {
  const firstImage = (log.imageUrls && log.imageUrls[0]) || (log.photos && log.photos[0]) || log.image;
  const fullImageUrl = firstImage ? getFullImageUrl(firstImage) : null;

  return `
    <div style="
      padding: 12px;
      min-width: 180px;
      max-width: 250px;
      box-sizing: border-box;
      font-family: sans-serif;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    ">
      <h3 style="
        margin: 0 0 8px 0;
        font-size: 14px;
        font-weight: bold;
        color: #333;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      ">${log.title || log.locationName || "Travel Location"}</h3>
      ${
        fullImageUrl
          ? `
        <img
          src="${fullImageUrl}"
          style="
            width: 100%;
            height: auto;
            max-height: 120px;
            object-fit: cover;
            border-radius: 4px;
            margin-bottom: 8px;
          "
          onerror="this.style.display='none'"
        />
      `
          : ""
      }
      <p style="
        margin: 0;
        font-size: 12px;
        color: #666;
        line-height: 1.4;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      ">${log.description || log.theme || "설명 없음"}</p>
    </div>
  `;
};

// ==================== Lifecycle ====================
// 컴포넌트 마운트 시 카카오맵 초기화
onMounted(async () => {
  try {
    // 여행 로그 불러오기
    // Travel Map 모드에서도 로그인 시 내 여행 기록 불러오기 (Recent Travel Logs 표시용)
    if (store.isLoggedIn) {
      await fetchMyTravelLogs();
    }

    // Travel Map 모드일 때는 전체 공개 여행 기록도 불러오기
    if (!isMyMap.value) {
      await fetchAllTravelLogs();
    }

    await loadKakaoMapScript();
    initializeMap();
  } catch (error) {
    console.error("카카오맵 로드 실패:", error);
  }
});

// 로그인 상태 변화 감지 - 로그인 시 내 여행 기록 불러오기
watch(() => store.isLoggedIn, async (newValue, oldValue) => {
  // 로그인 상태로 변경되었을 때만 실행
  if (newValue && !oldValue) {
    await fetchMyTravelLogs();
  }
});
</script>
