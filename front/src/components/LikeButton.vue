<template>
  <div class="flex items-center gap-2">
    <button
      @click="handleLikeClick"
      class="group relative flex items-center justify-center"
      :title="isLiked ? 'Unlike' : 'Like'"
    >
      <Heart
        :class="[
          'h-6 w-6 transition-all duration-300',
          isLiked
            ? 'fill-red-500 text-red-500 scale-125'
            : 'text-foreground/60 group-hover:scale-110 group-hover:text-red-500'
        ]"
      />
      <div
        v-if="isLiked"
        class="absolute inset-0 rounded-full border-2 border-red-500 animate-ping opacity-75"
      />
    </button>

    <span
      :class="[
        'text-sm font-semibold transition-all',
        animateCount ? 'scale-125 text-red-500' : '',
        isLiked ? 'text-red-500' : 'text-foreground'
      ]"
    >
      {{ likeCount }}
    </span>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Heart } from 'lucide-vue-next'
import { likeAPI } from '@/api/api'

const props = defineProps({
  postId: [String, Number],
  initialLikeCount: Number,
  initialIsLiked: Boolean
})

// 디버깅: LikeButton props 확인
console.log('LikeButton - postId:', props.postId)
console.log('LikeButton - initialIsLiked:', props.initialIsLiked)
console.log('LikeButton - initialLikeCount:', props.initialLikeCount)

const animateCount = ref(false)
const isLiked = ref(props.initialIsLiked || false)
const likeCount = ref(props.initialLikeCount || 0)

console.log('LikeButton - isLiked.value:', isLiked.value)
console.log('LikeButton - likeCount.value:', likeCount.value)

// props 변경 감지 - 페이지 이동 후 돌아왔을 때 업데이트
watch(() => props.initialIsLiked, (newValue) => {
  console.log('props.initialIsLiked 변경:', newValue)
  isLiked.value = newValue || false
})

watch(() => props.initialLikeCount, (newValue) => {
  console.log('props.initialLikeCount 변경:', newValue)
  likeCount.value = newValue || 0
})

const handleLikeClick = async () => {
  try {
    // API 호출
    const response = await likeAPI.toggleLike(props.postId)
    console.log('좋아요 응답:', response)

    // 백엔드 응답으로 상태 업데이트
    if (response.isLiked !== undefined) {
      isLiked.value = response.isLiked
    }
    if (response.likeCount !== undefined) {
      likeCount.value = response.likeCount
    }

    animateCount.value = true
    setTimeout(() => {
      animateCount.value = false
    }, 600)
  } catch (error) {
    console.error('좋아요 토글 실패:', error)
  }
}
</script>
