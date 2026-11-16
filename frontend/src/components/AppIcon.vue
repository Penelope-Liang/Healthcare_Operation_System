<script setup>
import { icons } from "./icons";

defineProps({ name: { type: String, required: true } });
</script>

<template>
  <!-- Icon markup is a fixed internal map, never user data. -->
  <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
       stroke-linecap="round" stroke-linejoin="round" aria-hidden="true" focusable="false"
       v-html="icons[name]"></svg>
</template>
