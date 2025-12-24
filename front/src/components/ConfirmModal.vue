<template>
  <Transition name="modal">
    <div
      v-if="isOpen"
      class="fixed inset-0 z-[60] flex items-center justify-center bg-black/50 backdrop-blur-[2px]"
      @click.self="onCancel"
    >
      <div
        class="bg-background rounded-lg max-w-md w-full mx-4 overflow-hidden shadow-2xl border border-border"
        @click.stop
      >
        <!-- 헤더 -->
        <div class="px-6 py-4 border-b border-border relative">
          <h3 class="text-lg font-semibold text-foreground">{{ title }}</h3>
          <button
            @click="onCancel"
            class="absolute top-4 right-4 text-foreground/60 hover:text-foreground transition-colors"
          >
            ×
          </button>
        </div>

        <!-- 내용 -->
        <div class="px-6 py-4">
          <p class="text-foreground/80">{{ message }}</p>
        </div>

        <!-- 버튼 -->
        <div class="px-6 py-4 border-t border-border flex gap-3 justify-end">
          <button
            @click="onCancel"
            class="px-4 py-2 rounded-lg bg-background hover:bg-muted text-foreground border border-border transition-colors"
          >
            {{ cancelText }}
          </button>
          <button
            @click="onConfirm"
            :class="[
              'px-4 py-2 rounded-lg font-medium transition-colors',
              danger
                ? 'bg-red-500 hover:bg-red-600 text-white'
                : 'bg-primary hover:bg-primary/90 text-primary-foreground',
            ]"
          >
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { onMounted, onUnmounted } from "vue";

const props = defineProps({
  isOpen: Boolean,
  title: {
    type: String,
    default: "확인",
  },
  message: {
    type: String,
    default: "계속 진행하시겠습니까?",
  },
  confirmText: {
    type: String,
    default: "확인",
  },
  cancelText: {
    type: String,
    default: "취소",
  },
  danger: {
    type: Boolean,
    default: false,
  },
  onConfirm: Function,
  onCancel: Function,
});

// ESC 키로 모달 닫기
const handleKeydown = (e) => {
  if (e.key === "Escape" && props.isOpen) {
    props.onCancel();
  }
};

onMounted(() => {
  document.addEventListener("keydown", handleKeydown);
});

onUnmounted(() => {
  document.removeEventListener("keydown", handleKeydown);
});
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
