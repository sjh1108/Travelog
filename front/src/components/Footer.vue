<template>
  <footer class="border-t border-border bg-background py-8 mt-12">
    <div class="mx-auto max-w-6xl px-4">
      <div class="grid grid-cols-1 gap-8 md:grid-cols-3">
        <div>
          <h3 class="text-lg font-bold text-foreground mb-3">Travelog</h3>
          <p class="text-foreground/60 text-sm">
            Share your travel stories and connect with adventurers around the world.
          </p>
        </div>
        <div>
          <h4 class="font-semibold text-foreground mb-3">Quick Links</h4>
          <ul class="space-y-2 text-sm text-foreground/60">
            <li>
              <router-link to="/feed" class="hover:text-primary transition-colors">
                Feed
              </router-link>
            </li>
            <li>
              <router-link to="/map" class="hover:text-primary transition-colors">
                Travel Map
              </router-link>
            </li>
            <li>
              <router-link to="/explore" class="hover:text-primary transition-colors">
                Explore
              </router-link>
            </li>
          </ul>
        </div>
        <div>
          <h4 class="font-semibold text-foreground mb-3">Legal</h4>
          <ul class="space-y-2 text-sm text-foreground/60">
            <li>
              <a href="#" class="hover:text-primary transition-colors">
                Privacy Policy
              </a>
            </li>
            <li>
              <a href="#" class="hover:text-primary transition-colors">
                Terms of Service
              </a>
            </li>
            <li>
              <a href="#" class="hover:text-primary transition-colors">
                Contact
              </a>
            </li>
          </ul>
        </div>
      </div>
      <div class="border-t border-border mt-8 pt-8 text-center text-sm text-foreground/40">
        <p>&copy; 2025 Travelog. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>
