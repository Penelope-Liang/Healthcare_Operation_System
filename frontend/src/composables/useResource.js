import { ref, onMounted } from "vue";

/** Loads data from the API on mount and exposes loading / error state plus a reload(). */
export function useResource(loader, initial = null) {
  const data = ref(initial);
  const loading = ref(true);
  const error = ref(null);

  async function reload() {
    loading.value = data.value === initial;
    error.value = null;
    try {
      data.value = await loader();
    } catch (e) {
      error.value = e;
    } finally {
      loading.value = false;
    }
  }

  onMounted(reload);
  return { data, loading, error, reload };
}
