<template>
  <Transition name="modal">
    <div
      v-if="isOpen"
      class="fixed inset-0 z-[70] flex items-center justify-center bg-black/50 backdrop-blur-sm"
      @click.self="handleClose"
    >
      <div
        class="bg-background rounded-lg max-w-sm w-full mx-4 overflow-hidden shadow-2xl border border-border"
        @click.stop
      >
        <!-- 내용 -->
        <div class="px-6 py-6 text-center">
          <div
            class="w-16 h-16 rounded-full mx-auto mb-4 flex items-center justify-center bg-green-100 text-green-600"
          >
            <svg
              class="w-8 h-8"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
            </svg>
          </div>
          <p class="text-lg font-semibold text-foreground mb-2">{{ message }}</p>
        </div>

        <!-- 버튼 -->
        <div class="px-6 pb-6">
          <button
            @click="handleClose"
            class="w-full px-4 py-2.5 rounded-lg bg-primary hover:bg-primary/90 text-primary-foreground font-medium transition-colors"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'

const props = defineProps({
  isOpen: Boolean,
  message: {
    type: String,
    default: ''
  },
  onClose: Function
})

const handleClose = () => {
  if (props.onClose) {
    props.onClose()
  }
}

// ESC 키로 모달 닫기
const handleKeydown = (e) => {
  if (e.key === 'Escape' && props.isOpen) {
    handleClose()
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
