<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-6xl mx-auto">
        <div class="flex justify-between items-center mb-8">
          <h1 class="text-3xl font-bold">
            {{ isMyMap ? "내 지도" : "여행 지도" }}
          </h1>
          <div class="flex gap-2">
            <button
              v-if="store.isLoggedIn && isMyMap"
              @click="showTravelForm = true"
              class="px-4 py-2 bg-secondary text-secondary-foreground rounded-lg hover:bg-secondary/90 transition-colors"
            >
              + 여행 기록 추가
            </button>
            <button
              @click="toggleMapMode"
              class="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
            >
              {{ isMyMap ? "여행 지도" : "내 지도" }}
            </button>
          </div>
        </div>
        <!-- 관광지 필터 메뉴 (Travel Map 모드일 때만 표시) -->
        <div v-if="!isMyMap" class="mb-4 bg-card border border-border rounded-lg p-4">
          <div class="flex flex-wrap items-end gap-3">
            <div class="flex-1 min-w-[150px]">
              <label class="block text-sm font-medium mb-2">시도 선택</label>
              <select
                v-model="selectedAreaCode"
                @change="handleAreaCodeChange"
                class="w-full px-3 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
              >
                <option value="">시도 선택</option>
                <option v-for="area in areaCodes" :key="area.code" :value="area.code">
                  {{ area.name }}
                </option>
              </select>
            </div>

            <div class="flex-1 min-w-[150px]">
              <label class="block text-sm font-medium mb-2">시군구 선택</label>
              <select
                v-model="selectedSigunguCode"
                :disabled="!selectedAreaCode"
                class="w-full px-3 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <option value="">시군구 선택</option>
                <option v-for="sigungu in sigunguCodes" :key="sigungu.code" :value="sigungu.code">
                  {{ sigungu.name }}
                </option>
              </select>
            </div>

            <div class="flex-1 min-w-[150px]">
              <label class="block text-sm font-medium mb-2">관광타입</label>
              <select
                v-model="selectedContentType"
                class="w-full px-3 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
              >
                <option value="">전체</option>
                <option value="12">관광지</option>
                <option value="14">문화시설</option>
                <option value="15">축제공연행사</option>
                <option value="25">여행코스</option>
                <option value="28">레포츠</option>
                <option value="32">숙박</option>
                <option value="38">쇼핑</option>
                <option value="39">음식점</option>
              </select>
            </div>

            <button
              @click="handleSearchTouristSpots"
              class="px-6 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-semibold whitespace-nowrap"
            >
              관광지 조회
            </button>

            <button
              @click="handleResetFilters"
              class="px-4 py-2 bg-secondary text-secondary-foreground rounded-lg hover:bg-secondary/90 transition-colors whitespace-nowrap"
            >
              초기화
            </button>
          </div>
        </div>

        <!-- 카카오맵 컨테이너 -->
        <div class="bg-card border border-border rounded-lg overflow-hidden">
          <div id="kakao-map" class="w-full h-[600px]"></div>
        </div>

        <!-- Travel Logs -->
        <div class="mt-8">
          <div class="mb-4">
            <div class="flex justify-between items-center mb-3">
              <h2 class="text-2xl font-bold">최근 여행 기록</h2>
              <!-- 기간별 필터 -->
              <div class="flex gap-2">
                <button
                  v-for="filter in periodFilters"
                  :key="filter.value"
                  @click="selectedPeriodFilter = filter.value"
                  :class="[
                    'px-4 py-2 text-sm rounded-lg transition-colors',
                    selectedPeriodFilter === filter.value
                      ? 'bg-primary text-primary-foreground'
                      : 'bg-muted hover:bg-muted/80'
                  ]"
                >
                  {{ filter.label }}
                </button>
              </div>
            </div>

            <!-- 기간 선택 입력 필드 -->
            <div
              v-if="selectedPeriodFilter === 'custom'"
              class="flex justify-end gap-3 items-center"
            >
              <div class="flex items-center gap-2">
                <label class="text-sm font-medium">시작일:</label>
                <input
                  v-model="customStartDate"
                  type="date"
                  :max="todayDate"
                  class="px-3 py-1.5 text-sm bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
                />
              </div>
              <div class="flex items-center gap-2">
                <label class="text-sm font-medium">종료일:</label>
                <input
                  v-model="customEndDate"
                  type="date"
                  :max="todayDate"
                  :min="customStartDate"
                  class="px-3 py-1.5 text-sm bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
                />
              </div>
            </div>
          </div>
          <div
            v-if="recentTravelLogs.length === 0"
            class="text-center py-8 text-foreground/50"
          >
            아직 여행 기록이 없습니다
          </div>
          <div
            v-else
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4"
          >
            <div
              v-for="log in recentTravelLogs"
              :key="log.id"
              class="bg-card border border-border rounded-lg overflow-hidden hover:shadow-lg transition-shadow cursor-pointer travel-log-card"
              @click="openTravelDetail(log)"
            >
              <!-- 대표 이미지 썸네일 -->
              <div class="relative w-full h-48 bg-muted">
                <img
                  :src="getFullImageUrl(getLogImages(log)[0])"
                  :alt="log.title || 'Travel photo'"
                  class="w-full h-full object-cover"
                  @error="$event.target.src = '/placeholder.svg'"
                />
                <!-- 사진 개수 표시 -->
                <div
                  v-if="getLogImages(log).length > 1"
                  class="absolute bottom-2 right-2 bg-black/70 text-white text-xs px-2 py-1 rounded"
                >
                  +{{ getLogImages(log).length - 1 }} more
                </div>
                <!-- 삭제 버튼 -->
                <button
                  @click.stop="handleDeleteTravelLog(log.id)"
                  class="travel-delete-btn absolute top-2 right-2 px-3 py-1 text-xs bg-red-500 text-white rounded-lg hover:bg-red-600 transition-opacity shadow-lg"
                  title="여행 기록 삭제"
                >
                  삭제
                </button>
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

          <!-- 더보기 버튼 -->
          <div
            v-if="filteredTravelLogs.length > displayedLogsCount"
            class="text-center mt-6"
          >
            <button
              @click="showMoreLogs"
              class="px-6 py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
            >
              더보기 ({{ filteredTravelLogs.length - displayedLogsCount }}개 더 있음)
            </button>
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
              <label class="block text-sm font-medium mb-2">썸네일 사진 (1장)</label>
              <div
                class="border-2 border-dashed border-border rounded-lg p-6 text-center cursor-pointer hover:border-primary transition-colors"
                @click="triggerFileInput"
                @dragover.prevent
                @drop.prevent="handleFileDrop"
              >
                <p class="text-foreground/60 mb-2">
                  썸네일 사진을 드래그하거나 클릭하여 선택하세요
                </p>
                <p class="text-xs text-foreground/40">PNG, JPG 형식 (1장만 가능)</p>
                <input
                  ref="fileInput"
                  type="file"
                  accept="image/*"
                  class="hidden"
                  @change="handleFileSelect"
                />
              </div>

              <!-- 미리보기 -->
              <div v-if="previewUrls.length > 0" class="mt-4">
                <div class="relative group inline-block">
                  <img
                    :src="previewUrls[0]"
                    alt="Preview"
                    class="w-full max-w-md h-48 object-cover rounded-lg"
                  />
                  <button
                    type="button"
                    @click="removeImage(0)"
                    class="absolute top-2 right-2 bg-red-500 text-white rounded-full w-8 h-8 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
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

            <!-- 주소 검색 -->
            <div>
              <label class="block text-sm font-medium mb-2">여행 장소 *</label>
              <div class="flex gap-2">
                <input
                  v-model="travelForm.address"
                  type="text"
                  required
                  readonly
                  placeholder="주소 검색 버튼을 클릭하세요"
                  class="flex-1 px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20 cursor-pointer"
                  @click="openAddressSearch"
                />
                <button
                  type="button"
                  @click="openAddressSearch"
                  class="px-4 py-2 bg-secondary text-secondary-foreground rounded-lg hover:bg-secondary/90 transition-colors whitespace-nowrap"
                >
                  주소 검색
                </button>
              </div>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium mb-2"
                  >시작일 *</label
                >
                <input
                  v-model="travelForm.startDate"
                  type="date"
                  :max="todayDate"
                  required
                  class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
                />
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">종료일 *</label>
                <input
                  v-model="travelForm.endDate"
                  type="date"
                  :max="todayDate"
                  :min="travelForm.startDate"
                  required
                  class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
                />
              </div>
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

    <!-- 삭제 확인 모달 -->
    <ConfirmModal
      :is-open="showDeleteConfirm"
      title="여행 기록 삭제"
      message="정말로 이 여행 기록을 삭제하시겠습니까? 삭제된 데이터는 복구할 수 없습니다."
      confirm-text="삭제"
      cancel-text="취소"
      :danger="true"
      :on-confirm="confirmDeleteTravel"
      :on-cancel="() => showDeleteConfirm = false"
    />

    <!-- 정보 모달 -->
    <InfoModal
      :is-open="showInfoModal"
      :message="infoMessage"
      :on-close="closeInfoModal"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import { MapPin } from "lucide-vue-next";
import axios from "axios";
import Navigation from "@/components/Navigation.vue";
import Footer from "@/components/Footer.vue";
import TravelDetailModal from "@/components/TravelDetailModal.vue";
import ConfirmModal from "@/components/ConfirmModal.vue";
import InfoModal from "@/components/InfoModal.vue";
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

// 필터 관련 상태
const areaCodes = ref([]); // 시도 목록
const sigunguCodes = ref([]); // 시군구 목록
const selectedAreaCode = ref(''); // 선택된 시도 코드
const selectedSigunguCode = ref(''); // 선택된 시군구 코드
const selectedContentType = ref(''); // 선택된 관광타입

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
  address: "",
  latitude: null,
  longitude: null,
  startDate: "",
  endDate: "",
  totalCost: 0, // 기본값 유지 (백엔드 호환성)
  theme: "", // 기본값 유지 (백엔드 호환성)
  isPublic: true, // 항상 공개로 설정
});
const selectedFiles = ref([]); // 선택된 파일들
const previewUrls = ref([]); // 미리보기 URL들
const fileInput = ref(null);

// 상세 모달 상태
const selectedTravelId = ref(null);
const showTravelDetailModal = ref(false);

// 삭제 확인 모달 상태
const showDeleteConfirm = ref(false);
const travelToDelete = ref(null);

// 정보 모달 상태
const showInfoModal = ref(false);
const infoMessage = ref('');

const closeInfoModal = () => {
  showInfoModal.value = false;
  infoMessage.value = '';
};

// 여행 기록 표시 관련 상태
const displayedLogsCount = ref(6); // 처음 표시할 개수
const selectedPeriodFilter = ref('all'); // 선택된 기간 필터
const customStartDate = ref(''); // 커스텀 시작일
const customEndDate = ref(''); // 커스텀 종료일

// 기간 필터 옵션
const periodFilters = [
  { label: '전체', value: 'all' },
  { label: '1개월', value: '1month' },
  { label: '3개월', value: '3months' },
  { label: '6개월', value: '6months' },
  { label: '올해', value: 'thisYear' },
  { label: '기간 선택', value: 'custom' }
];

// ==================== Computed & Utility Functions ====================
// 이미지 추출 헬퍼 함수
const getLogImages = (log) => {
  if (!log) return [];

  // 1. imageUrls 필드 처리 (우선순위 1)
  if (log.imageUrls) {
    // 배열인 경우
    if (Array.isArray(log.imageUrls) && log.imageUrls.length > 0) {
      return log.imageUrls;
    }
    // JSON 문자열인 경우 파싱
    if (typeof log.imageUrls === 'string' && log.imageUrls.trim().startsWith('[')) {
      try {
        const parsed = JSON.parse(log.imageUrls);
        if (Array.isArray(parsed) && parsed.length > 0) return parsed;
      } catch (e) {
        console.warn('imageUrls 파싱 실패:', e);
      }
    }
    // 단일 문자열인 경우
    if (typeof log.imageUrls === 'string' && log.imageUrls.trim() !== '') {
      return [log.imageUrls];
    }
  }

  // 2. images 필드 처리
  if (log.images) {
    if (Array.isArray(log.images) && log.images.length > 0) return log.images;
    if (typeof log.images === 'string' && log.images.trim().startsWith('[')) {
      try {
        const parsed = JSON.parse(log.images);
        if (Array.isArray(parsed) && parsed.length > 0) return parsed;
      } catch (e) {
        console.warn('images 파싱 실패:', e);
      }
    }
  }

  // 3. photos 필드 처리
  if (log.photos) {
    if (Array.isArray(log.photos) && log.photos.length > 0) return log.photos;
    if (typeof log.photos === 'string' && log.photos.trim().startsWith('[')) {
      try {
        const parsed = JSON.parse(log.photos);
        if (Array.isArray(parsed) && parsed.length > 0) return parsed;
      } catch (e) {
        console.warn('photos 파싱 실패:', e);
      }
    }
    if (typeof log.photos === 'string' && log.photos.trim() !== '') {
      return [log.photos];
    }
  }

  return [];
};

// 현재 지도 모드에 따라 표시할 여행 로그
const travelLogs = computed(() => {
  if (isMyMap.value) {
    return myTravelLogs.value;
  } else {
    return allTravelLogs.value;
  }
});

// 기간별로 필터링된 여행 로그
const filteredTravelLogs = computed(() => {
  // 로그인한 경우 내 여행 기록 표시
  const logsToShow = store.isLoggedIn ? myTravelLogs.value : allTravelLogs.value;

  if (!logsToShow || logsToShow.length === 0) return [];

  // 날짜를 기준으로 정렬 (최신순)
  const sorted = [...logsToShow].sort((a, b) => {
    const dateA = new Date(a.startDate || a.date || a.visitDate || 0);
    const dateB = new Date(b.startDate || b.date || b.visitDate || 0);
    return dateB - dateA;
  });

  // 기간 필터 적용
  if (selectedPeriodFilter.value === 'all') {
    return sorted;
  }

  // 커스텀 기간 선택
  if (selectedPeriodFilter.value === 'custom') {
    if (!customStartDate.value || !customEndDate.value) {
      return sorted; // 날짜가 선택되지 않으면 전체 표시
    }

    const startDate = new Date(customStartDate.value);
    const endDate = new Date(customEndDate.value);
    endDate.setHours(23, 59, 59, 999); // 종료일의 끝까지 포함

    return sorted.filter(log => {
      const logDate = new Date(log.startDate || log.date || log.visitDate || 0);
      return logDate >= startDate && logDate <= endDate;
    });
  }

  const now = new Date();
  const filterDate = new Date();

  switch (selectedPeriodFilter.value) {
    case '1month':
      filterDate.setMonth(now.getMonth() - 1);
      break;
    case '3months':
      filterDate.setMonth(now.getMonth() - 3);
      break;
    case '6months':
      filterDate.setMonth(now.getMonth() - 6);
      break;
    case 'thisYear':
      filterDate.setMonth(0);
      filterDate.setDate(1);
      break;
  }

  return sorted.filter(log => {
    const logDate = new Date(log.startDate || log.date || log.visitDate || 0);
    return logDate >= filterDate;
  });
});

// 표시할 여행 로그 (개수 제한 적용)
const recentTravelLogs = computed(() => {
  return filteredTravelLogs.value.slice(0, displayedLogsCount.value);
});

// 오늘 날짜 (YYYY-MM-DD 형식)
const todayDate = computed(() => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
});

// 여행 로그 불러오기 (내 여행 기록)
const fetchMyTravelLogs = async () => {
  try {
    const data = await travelAPI.getMyTravels();
    // console.log("🔍 여행 기록 데이터:", data);
    // if (data && data.length > 0) {
    //   console.log("🔍 첫 번째 여행 기록의 imageUrls:", data[0].imageUrls);
    //   console.log("🔍 imageUrls 타입:", typeof data[0].imageUrls);
    // }
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

// 날짜 포맷팅 (예: 2024년 1월 1일)
const formatDate = (dateString) => {
  if (!dateString) return "날짜 없음";

  const date = new Date(dateString);

  // Invalid Date 체크
  if (isNaN(date.getTime())) {
    return "잘못된 날짜";
  }

  return date.toLocaleDateString("ko-KR", {
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

    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${apiKey}&autoload=false&libraries=services`;
    script.onload = () => resolve();
    script.onerror = () => reject(new Error("Failed to load Kakao Map script"));
    document.head.appendChild(script);
  });
};

// 카카오맵 초기화 (초기 관광지는 표시하지 않음)
const initializeMap = async () => {
  window.kakao.maps.load(async () => {
    const container = document.getElementById("kakao-map");
    const options = {
      center: new window.kakao.maps.LatLng(36.3504, 127.3845), // 대전 중심
      level: 13, // 전국 지도가 보이는 확대 레벨
    };
    map.value = new window.kakao.maps.Map(container, options);

    // 초기에는 관광지 마커를 표시하지 않음 (필터링 후에만 표시)
  });
};

// ==================== API Functions ====================
// 시도 목록 조회 (한국관광공사 API - areaCode2)
const fetchAreaCodes = async () => {
  const apiKey = import.meta.env.VITE_TOUR_API_KEY;

  if (!apiKey || apiKey === "YOUR_TOUR_API_KEY") {
    console.error("공공데이터 API 키를 .env.local에 설정해주세요.");
    return [];
  }

  const params = {
    numOfRows: 120,
    pageNo: 1,
    MobileOS: "ETC",
    MobileApp: "Travelog",
    _type: "json",
  };

  const queryString = new URLSearchParams(params).toString();
  const url = `/api/tour/areaCode2?serviceKey=${apiKey}&${queryString}`;

  try {
    const response = await axios.get(url);
    const data = response.data;

    if (data.response?.body?.items?.item) {
      const items = Array.isArray(data.response.body.items.item)
        ? data.response.body.items.item
        : [data.response.body.items.item];

      return items.map((item) => ({
        code: item.code,
        name: item.name,
      }));
    }
    return [];
  } catch (error) {
    console.error("시도 목록 조회 실패:", error);
    return [];
  }
};

// 시군구 목록 조회 (한국관광공사 API - areaCode2)
const fetchSigunguCodes = async (areaCode) => {
  const apiKey = import.meta.env.VITE_TOUR_API_KEY;

  if (!apiKey || apiKey === "YOUR_TOUR_API_KEY") {
    console.error("공공데이터 API 키를 .env.local에 설정해주세요.");
    return [];
  }

  const params = {
    numOfRows: 120,
    pageNo: 1,
    MobileOS: "ETC",
    MobileApp: "Travelog",
    _type: "json",
    areaCode: areaCode,
  };

  const queryString = new URLSearchParams(params).toString();
  const url = `/api/tour/areaCode2?serviceKey=${apiKey}&${queryString}`;

  try {
    const response = await axios.get(url);
    const data = response.data;

    if (data.response?.body?.items?.item) {
      const items = Array.isArray(data.response.body.items.item)
        ? data.response.body.items.item
        : [data.response.body.items.item];

      return items.map((item) => ({
        code: item.code,
        name: item.name,
      }));
    }
    return [];
  } catch (error) {
    console.error("시군구 목록 조회 실패:", error);
    return [];
  }
};

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

      // CustomOverlay를 사용하여 정보창 생성
      let customOverlay = null;
      let mouseoutTimer = null;

      // 마우스 오버 시 정보창 표시
      window.kakao.maps.event.addListener(marker, "mouseover", () => {
        // mouseout 타이머가 있으면 취소
        if (mouseoutTimer) {
          clearTimeout(mouseoutTimer);
          mouseoutTimer = null;
        }

        // 기존 오버레이가 있으면 제거
        if (customOverlay) {
          customOverlay.setMap(null);
        }

        // CustomOverlay 생성
        customOverlay = new window.kakao.maps.CustomOverlay({
          position: markerPosition,
          content: createInfoWindowContent(spot),
          yAnchor: 3.1, // 마커와 겹치지 않도록 충분히 위쪽에 표시
          zIndex: 3
        });

        customOverlay.setMap(map.value);
      });

      // 마우스 아웃 시 정보창 숨김 (약간의 지연을 주어 버벅거림 방지)
      window.kakao.maps.event.addListener(marker, "mouseout", () => {
        mouseoutTimer = setTimeout(() => {
          if (customOverlay) {
            customOverlay.setMap(null);
          }
        }, 100);
      });

      // 마커 클릭 시 이미지 갤러리 모달 열기
      window.kakao.maps.event.addListener(marker, "click", async () => {
        // 정보창 즉시 닫기
        if (customOverlay) {
          customOverlay.setMap(null);
        }
        if (mouseoutTimer) {
          clearTimeout(mouseoutTimer);
          mouseoutTimer = null;
        }

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
      pointer-events: none;
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

// ==================== Filter Functions ====================
// 시도 선택 변경 시
const handleAreaCodeChange = async () => {
  // 시군구 초기화
  selectedSigunguCode.value = '';
  sigunguCodes.value = [];

  // 시도가 선택되었으면 시군구 목록 불러오기
  if (selectedAreaCode.value) {
    sigunguCodes.value = await fetchSigunguCodes(selectedAreaCode.value);
  }
};

// 관광지 조회 버튼 클릭
const handleSearchTouristSpots = async () => {
  // 기존 마커 제거
  markers.value.forEach((marker) => marker.setMap(null));
  markers.value = [];
  touristSpots.value = [];

  // 필터 선택 여부 확인
  const missingFilters = [];
  if (!selectedAreaCode.value) missingFilters.push('시도');
  if (!selectedSigunguCode.value) missingFilters.push('시군구');
  if (!selectedContentType.value) missingFilters.push('관광타입');

  // 필수 필터가 선택되지 않은 경우 모달 표시
  if (missingFilters.length > 0) {
    infoMessage.value = `${missingFilters.join(', ')}를 선택해주세요.`;
    showInfoModal.value = true;
    return;
  }

  const queryParams = {};

  // 시도 코드
  if (selectedAreaCode.value) {
    queryParams.areaCode = selectedAreaCode.value;
  }

  // 시군구 코드
  if (selectedSigunguCode.value) {
    queryParams.sigunguCode = selectedSigunguCode.value;
  }

  // 관광타입
  if (selectedContentType.value) {
    queryParams.contentTypeId = selectedContentType.value;
  }

  // 관광지 조회
  const spots = await fetchTouristSpots(queryParams);
  touristSpots.value = spots;
  updateMapMarkers(spots);
};

// 필터 초기화
const handleResetFilters = () => {
  selectedAreaCode.value = '';
  selectedSigunguCode.value = '';
  selectedContentType.value = '';
  sigunguCodes.value = [];

  // 마커 제거하고 빈 지도로 초기화
  markers.value.forEach((marker) => marker.setMap(null));
  markers.value = [];
  touristSpots.value = [];

  // 지도를 초기 위치로 이동
  if (map.value) {
    const center = new window.kakao.maps.LatLng(36.3504, 127.3845);
    map.value.setCenter(center);
    map.value.setLevel(13);
  }
};

// ==================== Modal Functions ====================
// 모달 닫기
const closeModal = () => {
  showImageModal.value = false;
  showFullImage.value = false;
};

// ESC 키로 모달 닫기
const handleKeydown = (e) => {
  if (e.key === 'Escape') {
    if (showImageModal.value || showFullImage.value) {
      closeModal();
    }
  }
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

// 파일 선택 처리 (단일 이미지만)
const handleFileSelect = (event) => {
  const files = Array.from(event.target.files || []);
  if (files.length > 0) {
    addFile(files[0]);
  }
};

// 드래그 앤 드롭 처리 (단일 이미지만)
const handleFileDrop = (event) => {
  const files = Array.from(event.dataTransfer?.files || []);
  const imageFiles = files.filter(file => file.type.startsWith('image/'));
  if (imageFiles.length > 0) {
    addFile(imageFiles[0]);
  }
};

// 파일 추가 로직 (기존 이미지 교체)
const addFile = (file) => {
  if (file.type.startsWith('image/')) {
    // 기존 이미지가 있으면 URL 해제
    if (previewUrls.value.length > 0) {
      URL.revokeObjectURL(previewUrls.value[0]);
    }
    // 새 이미지로 교체
    selectedFiles.value = [file];
    previewUrls.value = [URL.createObjectURL(file)];
  }
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

// 여행 기록 삭제 (모달 열기)
const handleDeleteTravelLog = (travelId) => {
  travelToDelete.value = travelId;
  showDeleteConfirm.value = true;
};

// 여행 기록 삭제 확인
const confirmDeleteTravel = async () => {
  if (!travelToDelete.value) return;

  try {
    await travelAPI.deleteTravel(travelToDelete.value);
    showDeleteConfirm.value = false;

    // 내 여행 기록 다시 불러오기
    await fetchMyTravelLogs();

    // My Map 모드일 때 지도 마커 업데이트
    if (isMyMap.value) {
      updateMyMapMarkers();
    }

    // 삭제 성공 메시지 표시
    infoMessage.value = '여행 기록이 삭제되었습니다.';
    showInfoModal.value = true;
  } catch (error) {
    console.error("여행 기록 삭제 실패:", error);
    alert("여행 기록 삭제에 실패했습니다.");
  } finally {
    travelToDelete.value = null;
  }
};

// 더보기 버튼 클릭
const showMoreLogs = () => {
  displayedLogsCount.value += 6; // 6개씩 더 표시
};

// 여행 로그 사진 앨범 열기
const openPhotoAlbum = (log, event) => {
  // 이벤트 버블링 방지 (카드 클릭 이벤트와 충돌 방지)
  event.stopPropagation();

  selectedSpot.value = {
    title: log.title || log.locationName,
    address: log.address || log.description,
  };

  // 여행 로그의 모든 이미지를 추출하여 변환
  const images = getLogImages(log);
  selectedSpotImages.value = images.map((photo) => ({
    originimgurl: getFullImageUrl(photo),
    smallimageurl: getFullImageUrl(photo),
  }));

  currentImageIndex.value = 0;
  showImageModal.value = true;
};

// 주소 검색 팝업 열기
const openAddressSearch = () => {
  new window.daum.Postcode({
    oncomplete: function(data) {
      // 선택한 주소 정보 저장
      const fullAddress = data.roadAddress || data.jibunAddress;
      travelForm.value.address = fullAddress;

      // 주소를 좌표로 변환
      const geocoder = new window.kakao.maps.services.Geocoder();
      geocoder.addressSearch(fullAddress, function(result, status) {
        if (status === window.kakao.maps.services.Status.OK) {
          travelForm.value.latitude = parseFloat(result[0].y);
          travelForm.value.longitude = parseFloat(result[0].x);
          // console.log('주소 변환 성공:', {
          //   address: fullAddress,
          //   lat: travelForm.value.latitude,
          //   lng: travelForm.value.longitude
          // });
        } else {
          console.error('주소를 좌표로 변환하는데 실패했습니다.');
          alert('주소를 좌표로 변환하는데 실패했습니다. 다시 시도해주세요.');
        }
      });
    }
  }).open();
};

// 여행 기록 작성
const handleCreateTravel = async () => {
  isSubmitting.value = true;

  try {
    let imageUrls = [];

    // 선택된 이미지가 있으면 업로드 (단일 이미지)
    if (selectedFiles.value.length > 0) {
      try {
        // console.log('이미지 업로드 시작...');
        const uploadResult = await fileAPI.uploadImage(selectedFiles.value[0]);
        // console.log('이미지 업로드 결과:', uploadResult);

        // 응답에서 URL 추출
        const imageUrl = uploadResult.url || uploadResult.data?.url || uploadResult.imageUrl || uploadResult;

        if (imageUrl && typeof imageUrl === 'string') {
          imageUrls = [imageUrl];
        }

//         console.log('처리된 이미지 URL:', imageUrls);
      } catch (uploadError) {
        console.warn('이미지 업로드 실패:', uploadError);
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

//     console.log('여행 기록 작성 데이터:', travelData);
    const newTravel = await travelAPI.createTravel(travelData);

    // 성공 후 폼 초기화
    travelForm.value = {
      title: "",
      address: "",
      latitude: null,
      longitude: null,
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

    // My Map 모드일 때 지도 마커 업데이트
    if (isMyMap.value) {
      updateMyMapMarkers();
    }
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

  // 위치 정보를 저장할 배열
  const positions = [];

  // 각 여행 로그에 대해 마커 생성 (실제 좌표가 있는 경우만)
  travelLogs.value.forEach((log) => {
    // 실제 좌표가 있는 경우만 마커 생성
    if (!log.latitude || !log.longitude) {
//       console.log(`여행 기록 "${log.title}"에 위치 정보가 없습니다.`);
      return;
    }

    const lat = log.latitude;
    const lng = log.longitude;

    positions.push({ lat, lng });

    const markerPosition = new window.kakao.maps.LatLng(lat, lng);
    const marker = new window.kakao.maps.Marker({
      position: markerPosition,
      title: log.title || log.locationName,
    });

    marker.setMap(map.value);
    markers.value.push(marker);

    // CustomOverlay를 사용하여 정보창 생성
    let customOverlay = null;
    let mouseoutTimer = null;

    // 마우스 오버 시 정보창 표시
    window.kakao.maps.event.addListener(marker, "mouseover", () => {
      // mouseout 타이머가 있으면 취소
      if (mouseoutTimer) {
        clearTimeout(mouseoutTimer);
        mouseoutTimer = null;
      }

      // 기존 오버레이가 있으면 제거
      if (customOverlay) {
        customOverlay.setMap(null);
      }

      // CustomOverlay 생성
      customOverlay = new window.kakao.maps.CustomOverlay({
        position: markerPosition,
        content: createMyMapInfoWindowContent(log),
        yAnchor: 3.1, // 마커와 겹치지 않도록 충분히 위쪽에 표시
        zIndex: 3
      });

      customOverlay.setMap(map.value);
    });

    // 마우스 아웃 시 정보창 숨김 (약간의 지연을 주어 버벅거림 방지)
    window.kakao.maps.event.addListener(marker, "mouseout", () => {
      mouseoutTimer = setTimeout(() => {
        if (customOverlay) {
          customOverlay.setMap(null);
        }
      }, 100);
    });

    // 마커 클릭 시 이미지 갤러리 모달 열기
    window.kakao.maps.event.addListener(marker, "click", async () => {
      // 정보창 즉시 닫기
      if (customOverlay) {
        customOverlay.setMap(null);
      }
      if (mouseoutTimer) {
        clearTimeout(mouseoutTimer);
        mouseoutTimer = null;
      }

      selectedSpot.value = {
        title: log.title || log.locationName,
        address: log.address || log.description,
      };

      // 여행 기록의 썸네일 이미지
      const recordImages = getLogImages(log);

      // 세부일정의 모든 이미지 가져오기
      let allImages = [...recordImages];

      try {
        // 세부일정 데이터 조회
        const details = await travelAPI.getTravelDetails(log.id);

        if (details && Array.isArray(details)) {
          // 각 세부일정의 이미지 추출
          details.forEach(detail => {
            const detailImages = getLogImages(detail);
            allImages = [...allImages, ...detailImages];
          });
        }
      } catch (error) {
        console.warn('세부일정 이미지 로드 실패:', error);
        // 에러가 나도 기본 이미지는 표시
      }

      // 모든 이미지를 갤러리 형식으로 변환
      selectedSpotImages.value = allImages.map((photo) => ({
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
  } else {
    // 위치 정보가 있는 여행 기록이 없으면 기본 위치(대전)로 설정
    const defaultCenter = new window.kakao.maps.LatLng(36.3504, 127.3845);
    map.value.setCenter(defaultCenter);
    map.value.setLevel(13);
  }
};

// My Map 정보창 HTML 생성 (Travel Map과 동일한 스타일)
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
      pointer-events: none;
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
    // 시도 목록 불러오기
    areaCodes.value = await fetchAreaCodes();

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

    // ESC 키 이벤트 리스너 등록
    document.addEventListener('keydown', handleKeydown);
  } catch (error) {
    console.error("카카오맵 로드 실패:", error);
  }
});

onUnmounted(() => {
  // ESC 키 이벤트 리스너 제거
  document.removeEventListener('keydown', handleKeydown);
});

// 로그인 상태 변화 감지 - 로그인 시 내 여행 기록 불러오기
watch(() => store.isLoggedIn, async (newValue, oldValue) => {
  // 로그인 상태로 변경되었을 때만 실행
  if (newValue && !oldValue) {
    await fetchMyTravelLogs();
  }
});

// 필터 변경 시 표시 개수 초기화
watch(selectedPeriodFilter, () => {
  displayedLogsCount.value = 6;
});

// 커스텀 날짜 변경 시 표시 개수 초기화
watch([customStartDate, customEndDate], () => {
  displayedLogsCount.value = 6;
});
</script>

<style scoped>
.travel-log-card .travel-delete-btn {
  opacity: 0;
}

.travel-log-card:hover .travel-delete-btn {
  opacity: 1;
}
</style>
