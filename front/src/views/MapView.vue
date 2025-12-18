<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-6xl mx-auto">
        <h1 class="text-3xl font-bold mb-8">Travel Map</h1>

        <!-- 카카오맵 컨테이너 -->
        <div class="bg-card border border-border rounded-lg overflow-hidden">
          <div id="kakao-map" class="w-full h-[600px]"></div>
        </div>

        <!-- Travel Logs -->
        <div class="mt-8">
          <h2 class="text-2xl font-bold mb-4">Your Travel Logs</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div
              v-for="log in travelLogs"
              :key="log.id"
              class="bg-card border border-border rounded-lg p-4 hover:shadow-lg transition-shadow"
            >
              <h3 class="font-bold mb-2">{{ log.locationName }}</h3>
              <p class="text-sm text-foreground/60 mb-2">{{ formatDate(log.date) }}</p>
              <p class="text-sm text-foreground/80">{{ log.description }}</p>
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
              <p class="text-sm text-foreground/60">{{ selectedSpot?.address }}</p>
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
                :alt="`${selectedSpot?.title} - 이미지 ${currentImageIndex + 1}`"
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
          <div v-if="selectedSpotImages.length > 1" class="grid grid-cols-4 md:grid-cols-6 gap-2">
            <div
              v-for="(image, index) in selectedSpotImages"
              :key="index"
              @click="currentImageIndex = index"
              :class="[
                'cursor-pointer rounded overflow-hidden border-2 transition-all',
                currentImageIndex === index ? 'border-primary' : 'border-transparent hover:border-foreground/30'
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
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { MapPin } from 'lucide-vue-next'
import axios from 'axios'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import { useAppStore } from '@/stores/app'
import { dummyTravelLogs } from '@/data/dummy-data'

// ==================== State Variables ====================
const store = useAppStore()

// 카카오맵 관련 상태
const map = ref(null) // 카카오맵 인스턴스
const markers = ref([]) // 지도에 표시된 마커 배열
const infoWindow = ref(null) // 마커 hover 시 표시되는 정보창

// 관광지 데이터
const touristSpots = ref([]) // 조회된 관광지 목록

// 이미지 갤러리 모달 상태
const selectedSpot = ref(null) // 선택된 관광지 정보
const selectedSpotImages = ref([]) // 선택된 관광지의 이미지 배열
const showImageModal = ref(false) // 이미지 갤러리 모달 표시 여부
const currentImageIndex = ref(0) // 현재 표시 중인 이미지 인덱스
const showFullImage = ref(false) // 원본 이미지 전체화면 표시 여부

// 여행 로그 초기화
if (store.travelLogs.length === 0) {
  store.setTravelLogs(dummyTravelLogs)
}

// ==================== Computed & Utility Functions ====================
const travelLogs = computed(() => store.travelLogs)

// 날짜 포맷팅 (예: January 1, 2024)
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

// ==================== Kakao Map Functions ====================
// 카카오맵 SDK 스크립트를 동적으로 로드
const loadKakaoMapScript = () => {
  return new Promise((resolve, reject) => {
    // 이미 로드되어 있는지 확인
    if (window.kakao && window.kakao.maps) {
      resolve()
      return
    }

    // 스크립트 태그 생성 및 삽입
    const script = document.createElement('script')
    const apiKey = import.meta.env.VITE_KAKAO_MAP_API_KEY

    if (!apiKey) {
      console.error('카카오맵 API 키가 설정되지 않았습니다. .env.local 파일을 확인해주세요.')
      reject(new Error('API key not found'))
      return
    }

    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${apiKey}&autoload=false`
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('Failed to load Kakao Map script'))
    document.head.appendChild(script)
  })
}

// 카카오맵 초기화 및 관광지 데이터 로드
const initializeMap = async () => {
  window.kakao.maps.load(async () => {
    const container = document.getElementById('kakao-map')
    const options = {
      center: new window.kakao.maps.LatLng(36.3504, 127.3845), // 대전 중심
      level: 13 // 전국 지도가 보이는 확대 레벨
    }
    map.value = new window.kakao.maps.Map(container, options)

    // 초기 관광지 목록 조회 및 마커 표시
    const spots = await fetchTouristSpots()
    touristSpots.value = spots
    updateMapMarkers(spots)
  })
}

// ==================== API Functions ====================
// 관광지 상세 이미지 조회 (한국관광공사 API - detailImage2)
const fetchTouristSpotImages = async (contentId) => {
  const apiKey = import.meta.env.VITE_TOUR_API_KEY

  if (!apiKey || apiKey === 'YOUR_TOUR_API_KEY') {
    console.error('공공데이터 API 키를 .env.local에 설정해주세요.')
    return []
  }

  const params = {
    numOfRows: 10,
    pageNo: 1,
    MobileOS: 'ETC',
    MobileApp: 'Travelog',
    _type: 'json',
    contentId: contentId,
    imageYN: 'Y'
  }

  const queryString = new URLSearchParams(params).toString()
  const url = `/api/tour/detailImage2?serviceKey=${apiKey}&${queryString}`

  try {
    const response = await axios.get(url)
    const data = response.data

    if (data.response?.body?.items?.item) {
      const items = Array.isArray(data.response.body.items.item)
        ? data.response.body.items.item
        : [data.response.body.items.item]

      return items.map(item => ({
        originimgurl: item.originimgurl, // 원본 이미지 URL
        smallimageurl: item.smallimageurl // 썸네일 이미지 URL
      }))
    }
    return []
  } catch (error) {
    console.error('관광지 이미지 조회 실패:', error)
    return []
  }
}

// 관광지 목록 조회 (한국관광공사 API - areaBasedList2)
const fetchTouristSpots = async (queryParams = {}) => {
  const apiKey = import.meta.env.VITE_TOUR_API_KEY

  if (!apiKey || apiKey === 'YOUR_TOUR_API_KEY') {
    console.error('공공데이터 API 키를 .env.local에 설정해주세요.')
    return []
  }

  const defaultParams = {
    numOfRows: 20, // 조회할 관광지 개수
    pageNo: 1,
    MobileOS: 'ETC',
    MobileApp: 'Travelog',
    _type: 'json',
    arrange: 'A' // 정렬 (A: 제목순)
  }

  const params = { ...defaultParams, ...queryParams }
  const queryString = new URLSearchParams(params).toString()
  const url = `/api/tour/areaBasedList2?serviceKey=${apiKey}&${queryString}`

  try {
    const response = await axios.get(url)
    const data = response.data

    if (data.response?.body?.items?.item) {
      const items = Array.isArray(data.response.body.items.item)
        ? data.response.body.items.item
        : [data.response.body.items.item]

      return items.map(item => ({
        id: item.contentid, // 관광지 고유 ID
        title: item.title, // 관광지 이름
        address: item.addr1, // 주소
        image: item.firstimage || item.firstimage2, // 대표 이미지
        mapx: parseFloat(item.mapx), // 경도
        mapy: parseFloat(item.mapy), // 위도
        contentTypeId: item.contenttypeid // 관광지 타입
      }))
    }
    return []
  } catch (error) {
    console.error('관광지 데이터 조회 실패:', error)
    return []
  }
}

// ==================== Map Marker Functions ====================
// 지도에 마커 추가 및 이벤트 등록
const updateMapMarkers = (spots) => {
  // 기존 마커 제거
  markers.value.forEach(marker => marker.setMap(null))
  markers.value = []

  // InfoWindow 인스턴스 생성 (hover 시 정보 표시용)
  if (!infoWindow.value) {
    infoWindow.value = new window.kakao.maps.InfoWindow({
      removable: false
    })
  }

  // 각 관광지에 대해 마커 생성
  spots.forEach(spot => {
    if (spot.mapx && spot.mapy) {
      const markerPosition = new window.kakao.maps.LatLng(spot.mapy, spot.mapx)
      const marker = new window.kakao.maps.Marker({
        position: markerPosition,
        title: spot.title
      })

      marker.setMap(map.value)
      markers.value.push(marker)

      // 마우스 오버 시 간단한 정보창 표시
      window.kakao.maps.event.addListener(marker, 'mouseover', () => {
        infoWindow.value.setContent(createInfoWindowContent(spot))
        infoWindow.value.open(map.value, marker)
      })

      // 마우스 아웃 시 정보창 숨김
      window.kakao.maps.event.addListener(marker, 'mouseout', () => {
        infoWindow.value.close()
      })

      // 마커 클릭 시 이미지 갤러리 모달 열기
      window.kakao.maps.event.addListener(marker, 'click', async () => {
        selectedSpot.value = spot
        const images = await fetchTouristSpotImages(spot.id)
        selectedSpotImages.value = images
        currentImageIndex.value = 0
        showImageModal.value = true
      })
    }
  })

  // 모든 마커가 보이도록 지도 범위 자동 조정
  if (spots.length > 0 && spots[0].mapx && spots[0].mapy) {
    const bounds = new window.kakao.maps.LatLngBounds()
    spots.forEach(spot => {
      if (spot.mapx && spot.mapy) {
        bounds.extend(new window.kakao.maps.LatLng(spot.mapy, spot.mapx))
      }
    })
    map.value.setBounds(bounds)
  }
}

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
      ${spot.image ? `
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
      ` : ''}
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
      ">${spot.address || '주소 정보 없음'}</p>
    </div>
  `
}

// ==================== Modal Functions ====================
// 모달 닫기
const closeModal = () => {
  showImageModal.value = false
  showFullImage.value = false
}

// 다음 이미지로 이동
const nextImage = () => {
  if (currentImageIndex.value < selectedSpotImages.value.length - 1) {
    currentImageIndex.value++
  }
}

// 이전 이미지로 이동
const prevImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--
  }
}

// 원본 이미지 전체화면으로 보기
const openFullImage = () => {
  showFullImage.value = true
}

// ==================== Lifecycle ====================
// 컴포넌트 마운트 시 카카오맵 초기화
onMounted(async () => {
  try {
    await loadKakaoMapScript()
    initializeMap()
  } catch (error) {
    console.error('카카오맵 로드 실패:', error)
  }
})
</script>
