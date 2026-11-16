<script setup>
import AppIcon from "./AppIcon.vue";
import { useToasts } from "@/composables/useToasts";

const { toasts, dismiss } = useToasts();
</script>

<template>
  <div class="toast-stack" aria-live="polite">
    <div v-for="toast in toasts" :key="toast.id" class="toast" :class="toast.kind"
         :role="toast.kind === 'error' ? 'alert' : 'status'">
      <AppIcon :name="toast.kind === 'error' ? 'alert' : 'check'" />
      <span>{{ toast.message }}</span>
      <button type="button" class="toast-close" aria-label="Dismiss" @click="dismiss(toast.id)">
        <AppIcon name="x" />
      </button>
    </div>
  </div>
</template>
