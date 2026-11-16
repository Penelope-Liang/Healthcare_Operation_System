<script setup>
import { nextTick, ref, useId, watch } from "vue";
import AppIcon from "./AppIcon.vue";

const props = defineProps({
  title: { type: String, required: true },
  description: { type: String, default: "" },
  submitLabel: { type: String, required: true },
  busy: { type: Boolean, default: false },
  error: { type: String, default: "" }
});
const emit = defineEmits(["submit"]);
const open = defineModel("open", { type: Boolean, default: false });

const dialog = ref(null);
const titleId = useId();

watch(open, async (isOpen) => {
  const el = dialog.value;
  if (!el) {
    return;
  }
  if (isOpen && !el.open) {
    el.showModal();
    await nextTick();
    el.querySelector("input:not([type=hidden]), select")?.focus();
  } else if (!isOpen && el.open) {
    el.close();
  }
});

function onBackdropClick(event) {
  // A click on the backdrop lands on the <dialog> element itself.
  if (event.target === dialog.value) {
    open.value = false;
  }
}
</script>

<template>
  <dialog ref="dialog" class="modal" :aria-labelledby="titleId" @close="open = false" @click="onBackdropClick">
    <form @submit.prevent="emit('submit')">
      <div class="modal-header">
        <div>
          <h2 :id="titleId">{{ title }}</h2>
          <p v-if="description">{{ description }}</p>
        </div>
        <button type="button" class="btn btn-icon" aria-label="Close" @click="open = false">
          <AppIcon name="x" />
        </button>
      </div>
      <div class="modal-body">
        <div v-if="error" class="form-alert">
          <AppIcon name="alert" /><span>{{ error }}</span>
        </div>
        <slot />
      </div>
      <div class="modal-footer">
        <button type="button" class="btn" @click="open = false">Cancel</button>
        <button type="submit" class="btn btn-primary" :disabled="busy">{{ submitLabel }}</button>
      </div>
    </form>
  </dialog>
</template>
