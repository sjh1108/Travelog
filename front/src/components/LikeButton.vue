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
            ? 'fill-secondary text-secondary scale-125'
            : 'text-foreground/60 group-hover:scale-110 group-hover:text-secondary'
        ]"
      />
      <div
        v-if="isLiked"
        class="absolute inset-0 rounded-full border-2 border-secondary animate-ping opacity-75"
      />
    </button>

    <span
      :class="[
        'text-sm font-semibold transition-all',
        animateCount ? 'scale-125 text-secondary' : '',
        isLiked ? 'text-secondary' : 'text-foreground'
      ]"
    >
      {{ likeCount }}
    </span>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Heart } from 'lucide-vue-next'
import { useAppStore } from '@/stores/app'
import { likeAPI } from '@/api/api'

const props = defineProps({
  postId: String,
  initialLikeCount: Number
})
const store = useAppStore()

const animateCount = ref(false)

const isLiked = computed(() => store.likedPosts.has(props.postId))
const likeCount = computed(() => props.initialLikeCount + (isLiked.value ? 1 : 0))

const handleLikeClick = async () => {
  try {
    // API 호출
    await likeAPI.toggleLike(props.postId)

    // 로컬 상태 업데이트
    store.toggleLike(props.postId)

    animateCount.value = true
    setTimeout(() => {
      animateCount.value = false
    }, 600)
  } catch (error) {
    console.error('좋아요 토글 실패:', error)
  }
}
</script>
