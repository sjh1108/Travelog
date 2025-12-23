<template>
  <Transition name="modal">
    <div
      v-if="isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      @click.self="onClose"
    >
      <div
        class="bg-background rounded-lg max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col"
        @click.stop
      >
        <!-- Header -->
        <div
          class="flex items-center justify-between p-4 border-b border-border"
        >
          <h2 class="text-lg font-semibold">Comments</h2>
          <button
            @click="onClose"
            class="text-foreground/60 hover:text-foreground transition-colors"
          >
            <X class="h-6 w-6" />
          </button>
        </div>

        <!-- Content -->
        <div class="flex-1 overflow-y-auto p-4">
          <div class="flex items-start gap-3 mb-4">
            <img
              :src="getImageUrl(post.user?.profileImage)"
              :alt="post.user?.nickname || 'User'"
              class="w-10 h-10 rounded-full"
            />
            <div>
              <p class="font-semibold">{{ post.user.nickname }}</p>
              <p class="text-sm text-foreground/80">{{ post.caption }}</p>
            </div>
          </div>

          <div v-if="comments.length === 0" class="text-center py-8">
            <p class="text-foreground/50">
              No comments yet. Be the first to comment!
            </p>
          </div>

          <div v-else class="space-y-4">
            <div
              v-for="comment in comments"
              :key="comment.id"
              class="flex items-start gap-3 group"
            >
              <img
                :src="getImageUrl(comment.user?.profileImage)"
                :alt="comment.user?.nickname || 'User'"
                class="w-8 h-8 rounded-full"
              />
              <div class="flex-1">
                <div class="flex items-start justify-between gap-2">
                  <div class="flex-1">
                    <p class="text-sm">
                      <span class="font-semibold">{{
                        comment.user.nickname
                      }}</span>
                      <span class="ml-2 text-foreground/80">{{
                        comment.content
                      }}</span>
                    </p>
                    <p class="text-xs text-foreground/50 mt-1">
                      {{ formatDate(comment.createdAt) }}
                    </p>
                  </div>
                  <button
                    v-if="canDeleteComment(comment)"
                    @click="handleDeleteComment(comment.id)"
                    class="text-red-500/60 hover:text-red-500 text-xs px-2 py-1 rounded hover:bg-red-500/10 transition-all flex-shrink-0"
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Comment Input -->
        <div class="border-t border-border p-4">
          <div class="flex gap-2">
            <input
              v-model="newComment"
              type="text"
              placeholder="Add a comment..."
              class="flex-1 px-4 py-2 bg-background border border-border rounded-full outline-none focus:ring-2 ring-primary/20"
              @keyup.enter="handleAddComment"
            />
            <button
              @click="handleAddComment"
              :disabled="!newComment.trim()"
              class="px-4 py-2 bg-primary text-primary-foreground rounded-full font-semibold disabled:opacity-50 disabled:cursor-not-allowed hover:opacity-90 transition-opacity"
            >
              Post
            </button>
          </div>
        </div>
      </div>
    </div>
  </Transition>

  <!-- 삭제 확인 모달 -->
  <ConfirmModal
    :is-open="showDeleteConfirm"
    title="댓글 삭제"
    message="정말로 이 댓글을 삭제하시겠습니까?"
    confirm-text="삭제"
    cancel-text="취소"
    :danger="true"
    :on-confirm="confirmDelete"
    :on-cancel="cancelDelete"
  />
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from "vue";
import { X } from "lucide-vue-next";
import { useAppStore } from "@/stores/app";
import { commentAPI } from "@/api/api";
import { getFullImageUrl, getProfileImageUrl } from "@/utils/imageUtils";
import ConfirmModal from "./ConfirmModal.vue";

const props = defineProps({
  post: Object,
  isOpen: Boolean,
  onClose: Function,
});

const store = useAppStore();
const newComment = ref("");
const isLoading = ref(false);
const showDeleteConfirm = ref(false);
const commentToDelete = ref(null);

// store에서 댓글 가져오기 (reactive)
const comments = computed(() => store.getComments(props.post?.id));

// 이미지 URL 처리 함수를 템플릿에서 사용 가능하도록 노출 (프로필 이미지용)
const getImageUrl = getProfileImageUrl;

// 댓글 불러오기
const fetchComments = async () => {
  if (!props.post?.id) return;

  try {
    isLoading.value = true;
    const data = await commentAPI.getComments(props.post.id);

    // 백엔드 응답을 프론트엔드 형식으로 변환
    const commentsWithUser = data.map((comment) => {
      // 이미 user 객체가 있으면 그대로 사용
      if (comment.user) {
        return comment;
      }

      // nickname, profileImage, writerEmail/userEmail이 있으면 user 객체로 변환
      const email = comment.writerEmail || comment.userEmail;
      if (comment.nickname || comment.profileImage || email) {
        return {
          ...comment,
          userId: email || comment.userId,
          user: {
            id: email || comment.userId,
            email: email || comment.userId,
            nickname: comment.nickname || "Unknown User",
            profileImage: comment.profileImage || "/default-profile.svg",
          },
        };
      }

      return comment;
    });

    // store에 댓글 저장 (comments는 computed이므로 자동으로 업데이트됨)
    store.setComments(props.post.id, commentsWithUser);
  } catch (error) {
    // 403 에러는 인증 문제이므로 인터셉터에서 처리됨
    if (error.response?.status !== 403) {
      console.error("댓글 조회 실패:", error);
    }
    store.setComments(props.post.id, []);
  } finally {
    isLoading.value = false;
  }
};

// 모달이 열릴 때 댓글 불러오기
watch(
  () => props.isOpen,
  (isOpen) => {
    if (isOpen) {
      fetchComments();
    }
  },
  { immediate: true }
);

// ESC 키로 모달 닫기
const handleKeydown = (e) => {
  if (e.key === "Escape" && props.isOpen) {
    props.onClose();
  }
};

onMounted(() => {
  document.addEventListener("keydown", handleKeydown);
});

onUnmounted(() => {
  document.removeEventListener("keydown", handleKeydown);
});

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const handleAddComment = async () => {
  if (!newComment.value.trim() || !store.currentUser) {
    return;
  }

  try {
    const commentData = {
      postId: props.post.id,
      content: newComment.value.trim(),
    };

    await commentAPI.createComment(commentData);

    // 댓글 목록 다시 불러오기
    await fetchComments();

    newComment.value = "";
  } catch (error) {
    console.error("댓글 작성 실패:", error);
  }
};

// 댓글 삭제 권한 확인 (본인이 작성한 댓글만 삭제 가능)
const canDeleteComment = (comment) => {
  if (!store.currentUser) return false;

  const currentUserId = store.currentUser.id || store.currentUser.email;
  const commentUserId =
    comment.user?.id ||
    comment.user?.email ||
    comment.userId ||
    comment.writerEmail ||
    comment.userEmail;

  return currentUserId === commentUserId;
};

// 댓글 삭제 확인 모달 열기
const handleDeleteComment = (commentId) => {
  commentToDelete.value = commentId;
  showDeleteConfirm.value = true;
};

// 댓글 삭제 확인
const confirmDelete = async () => {
  if (!commentToDelete.value) return;

  try {
    await commentAPI.deleteComment(commentToDelete.value);

    // 댓글 목록 다시 불러오기
    await fetchComments();
  } catch (error) {
    console.error("댓글 삭제 실패:", error);
  } finally {
    showDeleteConfirm.value = false;
    commentToDelete.value = null;
  }
};

// 댓글 삭제 취소
const cancelDelete = () => {
  showDeleteConfirm.value = false;
  commentToDelete.value = null;
};
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
</style>
