<template>
  <Transition name="modal">
    <div
      v-if="isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4"
      @click.self="onClose"
    >
      <div
        class="bg-background rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto shadow-2xl border border-border"
        @click.stop
      >
        <!-- 헤더 -->
        <div class="sticky top-0 bg-background border-b border-border p-6 z-10">
          <div class="flex justify-between items-start">
            <div class="flex-1">
              <h2 class="text-2xl font-bold mb-2">
                {{ travel?.title || "여행 상세" }}
              </h2>
              <p v-if="travel" class="text-sm text-foreground/60">
                {{ formatDate(travel.startDate) }}
                <span
                  v-if="travel.endDate && travel.endDate !== travel.startDate"
                >
                  ~ {{ formatDate(travel.endDate) }}
                </span>
              </p>
            </div>
            <button
              @click="onClose"
              class="text-foreground/60 hover:text-foreground text-2xl transition-colors"
            >
              ×
            </button>
          </div>
        </div>

        <!-- 이미지 갤러리 -->
        <div
          v-if="travel && travelImages.length > 0"
          class="p-6 border-b border-border"
        >
          <h3 class="text-lg font-semibold mb-4">사진</h3>

          <!-- 메인 이미지 -->
          <div class="relative mb-4">
            <img
              :src="getFullImageUrl(travelImages[currentImageIndex])"
              :alt="`Travel photo ${currentImageIndex + 1}`"
              class="w-full h-[400px] object-cover rounded-lg"
            />

            <!-- 좌우 화살표 -->
            <button
              v-if="currentImageIndex > 0"
              @click="prevImage"
              class="absolute left-2 top-1/2 -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white rounded-full w-10 h-10 flex items-center justify-center"
            >
              ‹
            </button>
            <button
              v-if="currentImageIndex < travelImages.length - 1"
              @click="nextImage"
              class="absolute right-2 top-1/2 -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white rounded-full w-10 h-10 flex items-center justify-center"
            >
              ›
            </button>

            <!-- 이미지 카운터 -->
            <div
              class="absolute bottom-2 right-2 bg-black/70 text-white text-sm px-3 py-1 rounded"
            >
              {{ currentImageIndex + 1 }} / {{ travelImages.length }}
            </div>
          </div>

          <!-- 썸네일 그리드 -->
          <div v-if="travelImages.length > 1" class="grid grid-cols-6 gap-2">
            <div
              v-for="(image, index) in travelImages"
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
                :src="getFullImageUrl(image)"
                :alt="`Thumbnail ${index + 1}`"
                class="w-full h-16 object-cover"
              />
            </div>
          </div>
        </div>

        <!-- 여행 정보 -->
        <div v-if="travel" class="p-6 border-b border-border">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold">여행 정보</h3>
            <button
              @click="handleDeleteTravel"
              class="px-4 py-2 text-sm bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
              title="여행 기록 삭제"
            >
              여행 기록 삭제
            </button>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-foreground/60 mb-1">기간</p>
              <p class="font-medium">
                {{ formatDate(travel.startDate) }}
                <span
                  v-if="travel.endDate && travel.endDate !== travel.startDate"
                >
                  ~ {{ formatDate(travel.endDate) }}
                </span>
              </p>
            </div>
            <div v-if="travel.theme">
              <p class="text-sm text-foreground/60 mb-1">테마</p>
              <p class="font-medium">{{ travel.theme }}</p>
            </div>
            <div v-if="travel.totalCost">
              <p class="text-sm text-foreground/60 mb-1">총 비용</p>
              <p class="font-medium">
                ₩{{ travel.totalCost.toLocaleString() }}
              </p>
            </div>
            <div>
              <p class="text-sm text-foreground/60 mb-1">공개 여부</p>
              <p class="font-medium">
                {{ travel.isPublic ? "공개" : "비공개" }}
              </p>
            </div>
          </div>
        </div>

        <!-- 상세 일정 목록 -->
        <div class="p-6 border-b border-border">
          <h3 class="text-lg font-semibold mb-4">상세 일정</h3>

          <div v-if="isLoadingDetails" class="text-center py-8">
            <div
              class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-border border-t-primary"
            ></div>
            <p class="text-sm text-foreground/50 mt-2">일정 불러오는 중...</p>
          </div>

          <div v-else-if="travelDetails.length === 0" class="text-center py-8">
            <p class="text-foreground/50">아직 추가된 일정이 없습니다</p>
          </div>

          <div v-else class="space-y-4">
            <div
              v-for="detail in travelDetails"
              :key="detail.id"
              class="bg-muted/50 rounded-lg p-4 relative detail-card"
            >
              <div class="flex justify-between items-start mb-2">
                <h4 class="font-semibold">{{ detail.locationName }}</h4>
                <button
                  @click="handleDeleteDetail(detail.id)"
                  class="detail-delete-btn px-2 py-1 text-xs bg-red-500 text-white rounded hover:bg-red-600 transition-opacity"
                  title="일정 삭제"
                >
                  삭제
                </button>
              </div>
              <p
                v-if="detail.description"
                class="text-sm text-foreground/80 mb-2"
              >
                {{ detail.description }}
              </p>
              <div
                v-if="detail.latitude && detail.longitude"
                class="text-xs text-foreground/60"
              >
                📍 {{ detail.latitude }}, {{ detail.longitude }}
              </div>

              <!-- 일정 사진 (Photo Album Style) -->
              <div v-if="getDetailImages(detail).length > 0" class="mt-3">
                <div class="grid grid-cols-4 gap-2">
                  <div
                    v-for="(url, idx) in getDetailImages(detail).slice(0, 4)"
                    :key="idx"
                    class="relative aspect-square cursor-pointer group overflow-hidden rounded-md"
                    @click="openLightbox(getDetailImages(detail), idx)"
                  >
                    <img
                      :src="getFullImageUrl(url)"
                      :alt="`Detail photo ${idx + 1}`"
                      class="w-full h-full object-cover transition-transform group-hover:scale-110"
                    />
                    <!-- 4번째 이미지에 +N 표시 -->
                    <div
                      v-if="idx === 3 && getDetailImages(detail).length > 4"
                      class="absolute inset-0 bg-black/60 flex items-center justify-center text-white font-bold text-lg"
                    >
                      +{{ getDetailImages(detail).length - 4 }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 새 일정 추가 폼 -->
        <div class="p-6">
          <button
            v-if="!showAddDetailForm"
            @click="showAddDetailForm = true"
            class="w-full py-3 border-2 border-dashed border-border rounded-lg text-foreground/60 hover:border-primary hover:text-primary transition-colors"
          >
            + 일정 추가
          </button>

          <form v-else @submit.prevent="handleAddDetail" class="space-y-4">
            <h3 class="text-lg font-semibold mb-4">새 일정 추가</h3>

            <div>
              <label class="block text-sm font-medium mb-2">장소 이름 *</label>
              <input
                v-model="detailForm.locationName"
                type="text"
                required
                placeholder="예: 경복궁"
                class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
              />
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium mb-2">위도</label>
                <input
                  v-model.number="detailForm.latitude"
                  type="number"
                  step="any"
                  placeholder="37.5796"
                  class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
                />
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">경도</label>
                <input
                  v-model.number="detailForm.longitude"
                  type="number"
                  step="any"
                  placeholder="126.9770"
                  class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium mb-2">설명</label>
              <textarea
                v-model="detailForm.description"
                rows="3"
                placeholder="이 장소에 대한 메모를 작성하세요..."
                class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20 resize-none"
              />
            </div>

            <!-- 이미지 업로드 -->
            <div>
              <label class="block text-sm font-medium mb-2">사진</label>
              <div
                class="border-2 border-dashed border-border rounded-lg p-4 text-center cursor-pointer hover:border-primary transition-colors"
                @click="triggerDetailFileInput"
                @dragover.prevent
                @drop.prevent="handleDetailFileDrop"
              >
                <p class="text-sm text-foreground/60">
                  클릭하거나 사진을 드래그하세요
                </p>
                <input
                  ref="detailFileInput"
                  type="file"
                  accept="image/*"
                  multiple
                  class="hidden"
                  @change="handleDetailFileSelect"
                />
              </div>

              <!-- 미리보기 -->
              <div
                v-if="detailPreviewUrls.length > 0"
                class="grid grid-cols-4 gap-2 mt-3"
              >
                <div
                  v-for="(url, index) in detailPreviewUrls"
                  :key="index"
                  class="relative group"
                >
                  <img
                    :src="url"
                    alt="미리보기"
                    class="w-full h-20 object-cover rounded"
                  />
                  <button
                    type="button"
                    @click="removeDetailImage(index)"
                    class="absolute top-1 right-1 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity text-xs"
                  >
                    ×
                  </button>
                </div>
              </div>
            </div>

            <div class="flex gap-2 justify-end">
              <button
                type="button"
                @click="cancelAddDetail"
                class="px-4 py-2 bg-background border border-border rounded-lg hover:bg-muted transition-colors"
              >
                취소
              </button>
              <button
                type="submit"
                :disabled="isSubmittingDetail"
                class="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors disabled:opacity-50"
              >
                {{ isSubmittingDetail ? "추가 중..." : "일정 추가" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </Transition>

  <!-- Lightbox (Full Screen Image Viewer) -->
  <div
    v-if="showLightbox"
    class="fixed inset-0 z-[60] bg-black/95 flex items-center justify-center p-4"
    @click="closeLightbox"
  >
    <button
      @click="closeLightbox"
      class="absolute top-4 right-4 text-white/70 hover:text-white text-4xl transition-colors"
    >
      ×
    </button>

    <div class="relative max-w-[90vw] max-h-[90vh]" @click.stop>
      <img
        :src="getFullImageUrl(lightboxImages[lightboxIndex])"
        :alt="`Photo ${lightboxIndex + 1}`"
        class="max-w-full max-h-[85vh] object-contain select-none"
      />

      <button
        v-if="lightboxIndex > 0"
        @click="prevLightboxImage"
        class="absolute left-[-50px] top-1/2 -translate-y-1/2 text-white/70 hover:text-white text-5xl p-2"
      >
        ‹
      </button>
      <button
        v-if="lightboxIndex < lightboxImages.length - 1"
        @click="nextLightboxImage"
        class="absolute right-[-50px] top-1/2 -translate-y-1/2 text-white/70 hover:text-white text-5xl p-2"
      >
        ›
      </button>

      <div class="text-center text-white/60 mt-4 text-sm font-medium">
        {{ lightboxIndex + 1 }} / {{ lightboxImages.length }}
      </div>
    </div>
  </div>

  <!-- 삭제 확인 모달 -->
  <ConfirmModal
    :is-open="showDeleteConfirm"
    :title="deleteConfirmTitle"
    :message="deleteConfirmMessage"
    confirm-text="삭제"
    cancel-text="취소"
    :danger="true"
    :on-confirm="confirmDelete"
    :on-cancel="() => showDeleteConfirm = false"
  />

  <!-- 정보 모달 -->
  <InfoModal
    :is-open="showInfoModal"
    :message="infoMessage"
    :on-close="closeInfoModal"
  />
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from "vue";
import { travelAPI, fileAPI } from "@/api/api";
import { useAppStore } from "@/stores/app";
import { getFullImageUrl } from "@/utils/imageUtils";
import ConfirmModal from "./ConfirmModal.vue";
import InfoModal from "./InfoModal.vue";

const props = defineProps({
  isOpen: Boolean,
  travelId: [Number, String],
  onClose: Function,
});

const store = useAppStore();

// 상태 변수
const travel = ref(null);
const travelDetails = ref([]);
const isLoadingDetails = ref(false);
const currentImageIndex = ref(0);

// 일정 추가 폼
const showAddDetailForm = ref(false);
const isSubmittingDetail = ref(false);
const detailForm = ref({
  locationName: "",
  latitude: null,
  longitude: null,
  description: "",
});
const detailFiles = ref([]);
const detailPreviewUrls = ref([]);
const detailFileInput = ref(null);

// Lightbox 상태
const showLightbox = ref(false);
const lightboxImages = ref([]);
const lightboxIndex = ref(0);

// 삭제 확인 모달 상태
const showDeleteConfirm = ref(false);
const deleteConfirmTitle = ref('');
const deleteConfirmMessage = ref('');
const deleteType = ref(''); // 'travel' or 'detail'
const itemToDelete = ref(null);

// 정보 모달 상태
const showInfoModal = ref(false);
const infoMessage = ref('');

const closeInfoModal = () => {
  showInfoModal.value = false;
  infoMessage.value = '';
};

// Computed
const travelImages = computed(() => {
  if (!travel.value) return [];

  // imageUrls 우선 처리
  if (travel.value.imageUrls) {
    // 배열인 경우
    if (Array.isArray(travel.value.imageUrls)) return travel.value.imageUrls;
    // JSON 문자열인 경우
    if (typeof travel.value.imageUrls === 'string' && travel.value.imageUrls.trim().startsWith('[')) {
      try {
        const parsed = JSON.parse(travel.value.imageUrls);
        if (Array.isArray(parsed)) return parsed;
      } catch (e) {
        console.warn('travel imageUrls 파싱 실패:', e);
      }
    }
    // 단일 문자열인 경우
    if (typeof travel.value.imageUrls === 'string' && travel.value.imageUrls.trim() !== '') {
      return [travel.value.imageUrls];
    }
  }

  // photos 처리
  if (travel.value.photos) {
    if (Array.isArray(travel.value.photos)) return travel.value.photos;
    if (typeof travel.value.photos === 'string' && travel.value.photos.trim().startsWith('[')) {
      try {
        const parsed = JSON.parse(travel.value.photos);
        if (Array.isArray(parsed)) return parsed;
      } catch (e) {
        console.warn('travel photos 파싱 실패:', e);
      }
    }
  }

  return [];
});

// Helper: 상세 일정 이미지 추출
const getDetailImages = (detail) => {
  if (!detail) return [];

  // 1. imageUrls 필드 처리 (우선순위 1)
  if (detail.imageUrls) {
    // 배열인 경우
    if (Array.isArray(detail.imageUrls) && detail.imageUrls.length > 0) {
      return detail.imageUrls.filter((img) => img && typeof img === "string");
    }
    // JSON 문자열인 경우 파싱
    if (typeof detail.imageUrls === "string" && detail.imageUrls.trim().startsWith("[")) {
      try {
        const parsed = JSON.parse(detail.imageUrls.trim());
        if (Array.isArray(parsed) && parsed.length > 0) {
          return parsed.filter((img) => img && typeof img === "string");
        }
      } catch (e) {
        console.warn("imageUrls 파싱 실패:", e);
      }
    }
    // 단일 문자열인 경우
    if (typeof detail.imageUrls === "string" && detail.imageUrls.trim() !== "") {
      return [detail.imageUrls.trim()];
    }
  }

  // 2. photos 필드 처리
  if (detail.photos) {
    if (Array.isArray(detail.photos) && detail.photos.length > 0) {
      return detail.photos.filter((img) => img && typeof img === "string");
    }
    if (typeof detail.photos === "string" && detail.photos.trim().startsWith("[")) {
      try {
        const parsed = JSON.parse(detail.photos.trim());
        if (Array.isArray(parsed) && parsed.length > 0) {
          return parsed.filter((img) => img && typeof img === "string");
        }
      } catch (e) {
        console.warn("photos 파싱 실패:", e);
      }
    }
    if (typeof detail.photos === "string" && detail.photos.trim() !== "") {
      return [detail.photos.trim()];
    }
  }

  // 3. images 필드 처리
  if (detail.images) {
    if (Array.isArray(detail.images) && detail.images.length > 0) {
      return detail.images.filter((img) => img && typeof img === "string");
    }
    if (typeof detail.images === "string" && detail.images.trim().startsWith("[")) {
      try {
        const parsed = JSON.parse(detail.images.trim());
        if (Array.isArray(parsed) && parsed.length > 0) {
          return parsed.filter((img) => img && typeof img === "string");
        }
      } catch (e) {
        console.warn("images 파싱 실패:", e);
      }
    }
  }

  return [];
};


// Lightbox 메서드
const openLightbox = (images, index) => {
  lightboxImages.value = images;
  lightboxIndex.value = index;
  showLightbox.value = true;
};

const closeLightbox = () => {
  showLightbox.value = false;
  lightboxImages.value = [];
  lightboxIndex.value = 0;
};

const nextLightboxImage = () => {
  if (lightboxIndex.value < lightboxImages.value.length - 1) {
    lightboxIndex.value++;
  }
};

const prevLightboxImage = () => {
  if (lightboxIndex.value > 0) {
    lightboxIndex.value--;
  }
};

// 키보드 이벤트 (Lightbox)
const handleLightboxKeydown = (e) => {
  if (!showLightbox.value) return;

  if (e.key === "Escape") closeLightbox();
  if (e.key === "ArrowRight") nextLightboxImage();
  if (e.key === "ArrowLeft") prevLightboxImage();
};

// 여행 정보 및 상세 일정 불러오기
watch(
  () => props.travelId,
  async (newId) => {
    if (newId) {
      currentImageIndex.value = 0;

      // 여행 정보 불러오기 (store에서)
      travel.value = store.travelLogs.find((log) => log.id === newId);

      // 상세 일정 불러오기
      await fetchTravelDetails();
    }
  },
  { immediate: true }
);

const fetchTravelDetails = async () => {
  if (!props.travelId) return;

  try {
    isLoadingDetails.value = true;
    const data = await travelAPI.getTravelDetails(props.travelId);
//     console.log("🔍 상세 일정 로드 완료:", data);
    if (data && data.length > 0) {
//       console.log("🔍 첫 번째 상세 일정의 imageUrls:", data[0].imageUrls);
//       console.log("🔍 imageUrls 타입:", typeof data[0].imageUrls);
    }
    travelDetails.value = data;
  } catch (error) {
    console.error("일정 조회 실패:", error);
    travelDetails.value = [];
  } finally {
    isLoadingDetails.value = false;
  }
};

// 이미지 갤러리 네비게이션
const nextImage = () => {
  if (currentImageIndex.value < travelImages.value.length - 1) {
    currentImageIndex.value++;
  }
};

const prevImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--;
  }
};

// 일정 사진 업로드 처리
const triggerDetailFileInput = () => {
  detailFileInput.value?.click();
};

const handleDetailFileSelect = (event) => {
  const files = Array.from(event.target.files || []);
  addDetailFiles(files);
};

const handleDetailFileDrop = (event) => {
  const files = Array.from(event.dataTransfer?.files || []);
  const imageFiles = files.filter((file) => file.type.startsWith("image/"));
  addDetailFiles(imageFiles);
};

const addDetailFiles = (files) => {
  files.forEach((file) => {
    if (file.type.startsWith("image/")) {
      detailFiles.value.push(file);
      detailPreviewUrls.value.push(URL.createObjectURL(file));
    }
  });
};

const removeDetailImage = (index) => {
  URL.revokeObjectURL(detailPreviewUrls.value[index]);
  detailFiles.value.splice(index, 1);
  detailPreviewUrls.value.splice(index, 1);
};

// 일정 추가
const handleAddDetail = async () => {
  isSubmittingDetail.value = true;

  try {
    let imageUrls = [];

    // 이미지 업로드
    if (detailFiles.value.length > 0) {
      try {
        const uploadResult = await fileAPI.uploadMultipleImages(
          detailFiles.value
        );
        imageUrls =
          uploadResult.urls || uploadResult.data?.urls || uploadResult || [];

        if (
          Array.isArray(imageUrls) &&
          imageUrls.length > 0 &&
          typeof imageUrls[0] === "object"
        ) {
          imageUrls = imageUrls.map((img) => img.url || img.imageUrl || img);
        }
      } catch (uploadError) {
        console.warn("이미지 업로드 실패:", uploadError);
      }
    }

    // sequenceOrder: 현재 일정 개수 + 1
    const sequenceOrder = travelDetails.value.length + 1;

    // latitude, longitude가 빈 값이거나 NaN이면 null로 설정
    const latitude =
      detailForm.value.latitude && !isNaN(detailForm.value.latitude)
        ? detailForm.value.latitude
        : null;
    const longitude =
      detailForm.value.longitude && !isNaN(detailForm.value.longitude)
        ? detailForm.value.longitude
        : null;

    const detailData = {
      locationName: detailForm.value.locationName,
      description: detailForm.value.description || "",
      latitude,
      longitude,
      sequenceOrder,
      imageUrls,
    };

//     console.log("일정 추가 요청 데이터:", detailData);

    // 백엔드가 단일 TravelDetailDto 객체를 기대하므로 배열로 감싸지 않음
    await travelAPI.createTravelDetails(props.travelId, detailData);

    // 폼 초기화
    cancelAddDetail();

    // 일정 목록 다시 불러오기
    await fetchTravelDetails();
  } catch (error) {
    console.error("일정 추가 실패:", error);
    console.error("에러 상세:", error.response?.data);
    console.error("에러 상태:", error.response?.status);

    const errorMsg = error.response?.data || "알 수 없는 오류가 발생했습니다.";
    alert(`일정 추가 실패: ${errorMsg}`);
  } finally {
    isSubmittingDetail.value = false;
  }
};

const cancelAddDetail = () => {
  detailForm.value = {
    locationName: "",
    latitude: null,
    longitude: null,
    description: "",
  };
  detailFiles.value.forEach((_, index) => {
    URL.revokeObjectURL(detailPreviewUrls.value[index]);
  });
  detailFiles.value = [];
  detailPreviewUrls.value = [];
  showAddDetailForm.value = false;
};

// 날짜 포맷팅
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return "";
  return date.toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
};

// 여행 기록 삭제 (모달 열기)
const handleDeleteTravel = () => {
  if (!props.travelId) return;

  deleteType.value = 'travel';
  deleteConfirmTitle.value = '여행 기록 삭제';
  deleteConfirmMessage.value = '정말로 이 여행 기록을 삭제하시겠습니까? 삭제된 데이터는 복구할 수 없습니다.';
  itemToDelete.value = props.travelId;
  showDeleteConfirm.value = true;
};

// 상세 일정 삭제 (모달 열기)
const handleDeleteDetail = (detailId) => {
  if (!props.travelId || !detailId) return;

  deleteType.value = 'detail';
  deleteConfirmTitle.value = '일정 삭제';
  deleteConfirmMessage.value = '이 일정을 삭제하시겠습니까?';
  itemToDelete.value = detailId;
  showDeleteConfirm.value = true;
};

// 삭제 확인
const confirmDelete = async () => {
  if (!itemToDelete.value) return;

  try {
    if (deleteType.value === 'travel') {
      await travelAPI.deleteTravel(itemToDelete.value);
      showDeleteConfirm.value = false;

      // 모달 닫기
      props.onClose();

      // 정보 모달 표시
      infoMessage.value = '여행 기록이 삭제되었습니다.';
      showInfoModal.value = true;

      // 페이지 새로고침
      setTimeout(() => {
        window.location.reload();
      }, 1500);
    } else if (deleteType.value === 'detail') {
      await travelAPI.deleteTravelDetail(props.travelId, itemToDelete.value);
      showDeleteConfirm.value = false;

      // 일정 목록 다시 불러오기
      await fetchTravelDetails();

      // 정보 모달 표시
      infoMessage.value = '일정이 삭제되었습니다.';
      showInfoModal.value = true;
    }
  } catch (error) {
    console.error("삭제 실패:", error);
    showDeleteConfirm.value = false;

    const errorMsg = deleteType.value === 'travel'
      ? '여행 기록 삭제에 실패했습니다.'
      : '일정 삭제에 실패했습니다.';
    alert(errorMsg);
  } finally {
    itemToDelete.value = null;
    deleteType.value = '';
  }
};

// ESC 키로 모달 닫기
const handleKeydown = (e) => {
  if (e.key === "Escape") {
    if (showLightbox.value) {
      closeLightbox();
    } else if (props.isOpen) {
      props.onClose();
    }
  }
};

onMounted(() => {
  document.addEventListener("keydown", handleKeydown);
  document.addEventListener("keydown", handleLightboxKeydown);
});

onUnmounted(() => {
  document.removeEventListener("keydown", handleKeydown);
  document.removeEventListener("keydown", handleLightboxKeydown);
});
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.line-clamp-1 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.line-clamp-2 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.detail-card .detail-delete-btn {
  opacity: 0;
}

.detail-card:hover .detail-delete-btn {
  opacity: 1;
}
</style>
