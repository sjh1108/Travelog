<template>
  <div class="fixed bottom-6 right-6 z-50">
    <!-- 토글 버튼 (오른쪽 하단 고정) -->
    <button
      @click="toggleChat"
      class="w-14 h-14 rounded-full bg-primary text-primary-foreground shadow-lg hover:shadow-xl transition-all duration-300 flex items-center justify-center hover:scale-110"
      :class="{ 'bg-accent text-accent-foreground': isOpen }"
      title="여행지 추천 챗봇"
    >
      <MessageSquare v-if="!isOpen" class="w-6 h-6" />
      <X v-else class="w-6 h-6" />
    </button>

    <!-- 챗봇 창 -->
    <transition name="slide-up">
      <div
        v-if="isOpen"
        class="absolute bottom-20 right-10 w-[480px] max-w-[calc(100vw-3rem)] h-[680px] max-h-[calc(100vh-7.5rem)] bg-background border border-border rounded-lg shadow-2xl flex flex-col overflow-hidden"
      >
        <!-- 헤더 -->
        <div
          class="bg-primary text-primary-foreground px-5 py-4 flex items-center justify-between -mx-px -mt-px rounded-t-lg"
        >
          <h3 class="text-lg font-semibold">여행지 추천 챗봇</h3>
          <button
            @click="toggleChat"
            class="p-1 hover:opacity-80 transition-opacity"
          >
            <X class="w-5 h-5" />
          </button>
        </div>

        <!-- 메시지 영역 -->
        <div
          ref="messagesContainer"
          class="flex-1 overflow-y-auto p-5 bg-muted/30 space-y-4 scrollbar-thin scrollbar-thumb-border scrollbar-track-transparent"
        >
          <!-- 초기 안내 메시지 -->
          <div v-if="messages.length === 0" class="flex justify-start">
            <div
              class="max-w-[85%] bg-card border border-border text-card-foreground px-4 py-3 rounded-lg rounded-bl-sm shadow-sm"
            >
              <p class="text-sm leading-relaxed">
                안녕하세요! 국내 여행지를 추천해드리는 챗봇입니다. 🗺️
                <br /><br />
                과거 여행 기록과 원하시는 테마를 입력해주시면, 최적의 여행지를
                추천해드립니다!
              </p>
            </div>
          </div>

          <!-- 메시지 목록 -->
          <div
            v-for="(message, index) in messages"
            :key="index"
            class="flex"
            :class="message.type === 'user' ? 'justify-end' : 'justify-start'"
          >
            <div
              v-if="message.type === 'user'"
              class="max-w-[80%] bg-primary text-primary-foreground px-4 py-3 rounded-lg rounded-br-sm shadow-sm"
            >
              <p class="text-sm leading-relaxed whitespace-pre-wrap">
                {{ message.content }}
              </p>
            </div>
            <div
              v-else
              class="max-w-[90%] bg-card border border-border text-card-foreground px-4 py-3 rounded-lg rounded-bl-sm shadow-sm"
            >
              <div
                class="text-sm leading-relaxed"
                v-html="formatBotMessage(message.content)"
              ></div>
            </div>
          </div>

          <!-- 로딩 인디케이터 -->
          <div v-if="isLoading" class="flex justify-start">
            <div
              class="bg-card border border-border px-4 py-3 rounded-lg rounded-bl-sm shadow-sm"
            >
              <div class="flex gap-1">
                <span
                  class="w-2 h-2 bg-primary rounded-full animate-bounce"
                  style="animation-delay: 0s"
                ></span>
                <span
                  class="w-2 h-2 bg-primary rounded-full animate-bounce"
                  style="animation-delay: 0.2s"
                ></span>
                <span
                  class="w-2 h-2 bg-primary rounded-full animate-bounce"
                  style="animation-delay: 0.4s"
                ></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 입력 영역 -->
        <div class="bg-background border-t border-border p-5 space-y-3">
          <!-- 여행 기록 입력 -->
          <div>
            <label
              class="block text-xs font-medium text-muted-foreground mb-1.5"
            >
              과거 여행 기록 (쉼표로 구분)
            </label>
            <input
              v-model="historyInput"
              type="text"
              placeholder="예: 제주도, 부산, 강릉"
              class="w-full px-3 py-2.5 bg-background border border-input rounded-md text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-all"
              @keydown.enter="sendMessage"
            />
          </div>

          <!-- 테마 입력 -->
          <div>
            <label
              class="block text-xs font-medium text-muted-foreground mb-1.5"
            >
              원하는 테마 (쉼표로 구분)
            </label>
            <input
              v-model="themeInput"
              type="text"
              placeholder="예: 힐링, 맛집, 바다"
              class="w-full px-3 py-2.5 bg-background border border-input rounded-md text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:border-transparent transition-all"
              @keydown.enter="sendMessage"
            />
          </div>

          <!-- 전송 버튼 -->
          <button
            @click="sendMessage"
            :disabled="
              isLoading || (!historyInput.trim() && !themeInput.trim())
            "
            class="w-full px-4 py-2.5 bg-primary text-primary-foreground rounded-md text-sm font-semibold flex items-center justify-center gap-2 hover:opacity-90 transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-sm hover:shadow-md"
          >
            <Send class="w-4 h-4" />
            <span>추천받기</span>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick } from "vue";
import { MessageSquare, X, Send } from "lucide-vue-next";
import { gmsAPI } from "@/api/api";

const isOpen = ref(false);
const isLoading = ref(false);
const historyInput = ref("");
const themeInput = ref("");
const messages = ref([]);
const messagesContainer = ref(null);

const toggleChat = () => {
  isOpen.value = !isOpen.value;
};

const sendMessage = async () => {
  if (
    isLoading.value ||
    (!historyInput.value.trim() && !themeInput.value.trim())
  ) {
    return;
  }

  // 사용자 입력을 배열로 변환 (쉼표로 구분)
  const histories = historyInput.value
    .split(",")
    .map((item) => item.trim())
    .filter((item) => item.length > 0);

  const themes = themeInput.value
    .split(",")
    .map((item) => item.trim())
    .filter((item) => item.length > 0);

  // 사용자 메시지 추가
  const userMessage = `여행 기록: ${
    histories.length > 0 ? histories.join(", ") : "없음"
  }\n테마: ${themes.length > 0 ? themes.join(", ") : "자유롭게 추천"}`;
  messages.value.push({
    type: "user",
    content: userMessage,
  });

  // 입력 필드 초기화
  historyInput.value = "";
  themeInput.value = "";

  // 로딩 시작
  isLoading.value = true;

  // 스크롤을 아래로
  await nextTick();
  scrollToBottom();

  try {
    // GMS API 호출
    const response = await gmsAPI.chat(histories, themes);

    // 봇 응답 추가
    messages.value.push({
      type: "bot",
      content: response,
    });
  } catch (error) {
    console.error("챗봇 응답 오류:", error);
    messages.value.push({
      type: "bot",
      content:
        "죄송합니다. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
    });
  } finally {
    isLoading.value = false;

    // 스크롤을 아래로
    await nextTick();
    scrollToBottom();
  }
};

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

// 봇 메시지 포맷팅 (줄바꿈과 기본 마크다운 처리)
const formatBotMessage = (content) => {
  return content
    .replace(/\n/g, "<br />")
    .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
    .replace(/\*(.*?)\*/g, "<em>$1</em>");
};
</script>

<style scoped>
/* 슬라이드 애니메이션 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 스크롤바 스타일 */
.scrollbar-thin::-webkit-scrollbar {
  width: 6px;
}

.scrollbar-track-transparent::-webkit-scrollbar-track {
  background: transparent;
}

.scrollbar-thumb-border::-webkit-scrollbar-thumb {
  background: hsl(var(--border));
  border-radius: 3px;
}

.scrollbar-thumb-border::-webkit-scrollbar-thumb:hover {
  background: hsl(var(--muted-foreground));
}
</style>
