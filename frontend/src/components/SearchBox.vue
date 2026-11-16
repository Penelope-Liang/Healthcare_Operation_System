<script setup>
import AppIcon from "./AppIcon.vue";

defineProps({ placeholder: { type: String, required: true } });
const query = defineModel({ type: String, default: "" });
</script>

<template>
  <label class="search">
    <AppIcon name="search" />
    <input v-model="query" type="search" :placeholder="placeholder" :aria-label="placeholder">
  </label>
</template>
