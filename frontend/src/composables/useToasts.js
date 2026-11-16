import { reactive } from "vue";

const toasts = reactive([]);
let nextId = 1;

function push(kind, message) {
  const id = nextId++;
  toasts.push({ id, kind, message });
  if (kind === "success") {
    setTimeout(() => dismiss(id), 5000);
  }
}

function dismiss(id) {
  const index = toasts.findIndex((t) => t.id === id);
  if (index >= 0) {
    toasts.splice(index, 1);
  }
}

/** App-wide toast notifications (success toasts auto-dismiss, errors stay until closed). */
export function useToasts() {
  return {
    toasts,
    dismiss,
    success: (message) => push("success", message),
    error: (message) => push("error", message)
  };
}
