<template>
  <nav class="sticky top-0 z-50 border-b border-border bg-background shadow">
    <div class="mx-auto max-w-6xl px-4 py-3">
      <div class="flex items-center justify-between gap-4">
        <!-- Logo -->
        <router-link to="/map" class="flex items-center gap-2">
          <MapPin class="h-7 w-7 text-primary" />
          <span class="text-2xl font-bold text-foreground">Travelog</span>
        </router-link>

        <!-- Search Bar -->
        <div class="hidden flex-1 md:block">
          <div class="relative">
            <Search class="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-foreground/40" />
            <input
              type="text"
              placeholder="Search locations, people..."
              class="w-full rounded-full bg-background/50 py-2 pl-10 pr-4 text-foreground placeholder-foreground/40 outline-none transition-all duration-200 hover:bg-background/70 focus:bg-background focus:ring-2 ring-primary/20"
            />
          </div>
        </div>

        <!-- Right Menu -->
        <div class="flex items-center gap-6">
          <router-link
            to="/upload"
            class="flex items-center gap-2 text-foreground transition-colors hover:text-primary"
            title="Upload Photo"
          >
            <Upload class="h-6 w-6" />
            <span class="hidden sm:inline">Upload</span>
          </router-link>

          <router-link
            to="/feed"
            class="flex items-center gap-2 text-foreground transition-colors hover:text-primary"
            title="Feed"
          >
            <Home class="h-6 w-6" />
            <span class="hidden sm:inline">Feed</span>
          </router-link>

          <router-link
            to="/notifications"
            class="relative text-foreground transition-colors hover:text-secondary"
            title="Notifications"
          >
            <Heart class="h-6 w-6" />
            <span class="absolute -right-2 -top-2 flex h-5 w-5 items-center justify-center rounded-full bg-secondary text-white text-xs font-bold">
              3
            </span>
          </router-link>

          <router-link
            to="/mypage"
            class="flex items-center gap-2 text-foreground transition-colors hover:text-primary"
            title="My Page"
          >
            <User class="h-6 w-6" />
            <span class="hidden sm:inline">Profile</span>
          </router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { Heart, Search, User, MapPin, Upload, Home } from 'lucide-vue-next'
</script>
