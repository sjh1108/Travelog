<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-6xl mx-auto">
        <h1 class="text-3xl font-bold mb-8">Travel Map</h1>
        
        <div class="bg-card border border-border rounded-lg p-6 min-h-[600px] flex items-center justify-center">
          <div class="text-center">
            <MapPin class="mx-auto h-16 w-16 text-foreground/40 mb-4" />
            <p class="text-foreground/60 mb-4">
              Map integration would go here
            </p>
            <p class="text-sm text-foreground/40">
              (Kakao Map API integration required)
            </p>
          </div>
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
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { MapPin } from 'lucide-vue-next'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import { useAppStore } from '@/stores/app'
import { dummyTravelLogs } from '@/data/dummy-data'

const store = useAppStore()

// Initialize travel logs
if (store.travelLogs.length === 0) {
  store.setTravelLogs(dummyTravelLogs)
}

const travelLogs = computed(() => store.travelLogs)

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}
</script>
