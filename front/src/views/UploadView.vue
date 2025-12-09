<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-2xl mx-auto">
        <h1 class="text-3xl font-bold mb-8">Upload Photo</h1>
        
        <div class="bg-card border border-border rounded-lg p-6">
          <!-- Image Upload Area -->
          <div
            class="border-2 border-dashed border-border rounded-lg p-8 text-center mb-6 cursor-pointer hover:border-primary transition-colors"
            @click="triggerFileInput"
            @dragover.prevent
            @drop.prevent="handleDrop"
          >
            <Upload class="mx-auto h-12 w-12 text-foreground/40 mb-4" />
            <p class="text-foreground/60 mb-2">
              Drag and drop your photo here, or click to select
            </p>
            <p class="text-sm text-foreground/40">PNG, JPG up to 10MB</p>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              class="hidden"
              @change="handleFileSelect"
            />
          </div>

          <!-- Preview -->
          <div v-if="previewUrl" class="mb-6">
            <img :src="previewUrl" alt="Preview" class="w-full rounded-lg" />
          </div>

          <!-- Form -->
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium mb-2">Location</label>
              <input
                v-model="location"
                type="text"
                placeholder="Where was this photo taken?"
                class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
              />
            </div>

            <div>
              <label class="block text-sm font-medium mb-2">Caption</label>
              <textarea
                v-model="caption"
                rows="4"
                placeholder="Write a caption..."
                class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20 resize-none"
              />
            </div>

            <button
              @click="handleUpload"
              :disabled="!previewUrl || !location"
              class="w-full py-3 bg-primary text-primary-foreground rounded-lg font-semibold disabled:opacity-50 disabled:cursor-not-allowed hover:opacity-90 transition-opacity"
            >
              Share Photo
            </button>
          </div>
        </div>
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Upload } from 'lucide-vue-next'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const store = useAppStore()

const fileInput = ref(null)
const previewUrl = ref('')
const location = ref('')
const caption = ref('')

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event) => {
  const target = event.target
  const file = target.files?.[0]
  if (file) {
    previewUrl.value = URL.createObjectURL(file)
  }
}

const handleDrop = (event) => {
  const file = event.dataTransfer?.files[0]
  if (file && file.type.startsWith('image/')) {
    previewUrl.value = URL.createObjectURL(file)
  }
}

const handleUpload = () => {
  if (!store.currentUser || !previewUrl.value || !location.value) return

  const newPost = {
    id: `post-${Date.now()}`,
    userId: store.currentUser.id,
    user: store.currentUser,
    imageUrl: previewUrl.value,
    travelLocation: location.value,
    caption: caption.value,
    likeCount: 0,
    commentCount: 0,
    isLiked: false,
    createdAt: new Date().toISOString(),
  }

  store.addPost(newPost)
  router.push('/feed')
}
</script>
