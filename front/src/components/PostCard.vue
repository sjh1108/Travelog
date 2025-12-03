<template>
  <article class="border border-border/40 rounded-lg overflow-hidden">
    <!-- Header -->
    <div class="flex items-center justify-between p-4 border-b border-border/40">
      <div class="flex items-center gap-3">
        <img
          :src="post.author.avatar"
          :alt="post.author.name"
          class="w-10 h-10 rounded-full object-cover"
        />
        <div>
          <p class="font-semibold">{{ post.author.name }}</p>
          <p class="text-xs text-muted-foreground">{{ post.timestamp }}</p>
        </div>
      </div>
      <button class="p-2 hover:bg-muted rounded-full transition-colors">
        <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
          <path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z" />
        </svg>
      </button>
    </div>

    <!-- Image -->
    <img
      :src="post.image"
      :alt="post.caption"
      class="w-full aspect-square object-cover"
    />

    <!-- Actions -->
    <div class="p-4 border-b border-border/40">
      <div class="flex items-center gap-4 mb-4">
        <button class="p-2 hover:bg-muted rounded-full transition-colors -ml-2">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
          </svg>
        </button>
        <button class="p-2 hover:bg-muted rounded-full transition-colors -ml-2">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
          </svg>
        </button>
        <button class="p-2 hover:bg-muted rounded-full transition-colors -ml-2">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
        </button>
      </div>
    </div>

    <!-- Stats -->
    <div class="px-4 py-2 border-b border-border/40">
      <p class="text-sm font-semibold">{{ post.likes }} likes</p>
    </div>

    <!-- Caption -->
    <div class="px-4 py-3 border-b border-border/40">
      <p class="text-sm">
        <span class="font-semibold">{{ post.author.name }}</span>
        {{ post.caption }}
      </p>
    </div>

    <!-- Comments -->
    <div class="px-4 py-3 border-b border-border/40">
      <p class="text-sm text-muted-foreground cursor-pointer hover:text-foreground">
        View all {{ post.comments }} comments
      </p>
    </div>

    <!-- Comment Input -->
    <div class="px-4 py-3 flex items-center gap-3">
      <input
        type="text"
        placeholder="Add a comment..."
        class="flex-1 bg-transparent text-sm outline-none placeholder-muted-foreground"
      />
      <button class="text-blue-500 text-sm font-semibold hover:text-blue-600">Post</button>
    </div>
  </article>
</template>

<script setup>
defineProps({
  post: {
    type: Object,
    required: true
  }
})
</script>
