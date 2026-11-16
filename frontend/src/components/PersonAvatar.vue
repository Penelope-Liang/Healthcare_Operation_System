<script setup>
import { computed } from "vue";

const props = defineProps({
  first: { type: String, required: true },
  last: { type: String, required: true }
});

// Colour comes from the name so a person keeps the same avatar between visits.
const tone = computed(() => `tone-${(props.first.length + props.last.length) % 6}`);
const initials = computed(() => `${props.first.charAt(0)}${props.last.charAt(0)}`.toUpperCase());
</script>

<template>
  <span class="avatar" :class="tone" aria-hidden="true">{{ initials }}</span>
</template>
