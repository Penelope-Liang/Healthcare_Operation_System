<script setup>
defineProps({
  label: { type: String, required: true },
  for: { type: String, required: true },
  error: { type: String, default: "" },
  optional: { type: Boolean, default: false },
  full: { type: Boolean, default: false }
});
</script>

<template>
  <div class="field" :class="{ full }">
    <label :for="$props.for">{{ label }} <span v-if="optional" class="optional">(optional)</span></label>
    <slot />
    <p v-if="error" class="field-error">{{ error }}</p>
  </div>
</template>
