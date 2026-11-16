import { reactive, ref } from "vue";
import { ApiError } from "@/lib/api";

/**
 * Form state for the create dialogs. submit() runs the API call and maps a 422 response's
 * fieldErrors onto the matching inputs; any other failure becomes a form-level message.
 */
export function useForm(initialValues) {
  const values = reactive({ ...initialValues });
  const errors = reactive({});
  const formError = ref("");
  const busy = ref(false);

  function clearErrors() {
    Object.keys(errors).forEach((k) => delete errors[k]);
    formError.value = "";
  }

  function reset() {
    Object.assign(values, initialValues);
    clearErrors();
  }

  async function submit(action) {
    clearErrors();
    busy.value = true;
    try {
      return await action({ ...values });
    } catch (e) {
      if (e instanceof ApiError && Object.keys(e.fieldErrors).length > 0) {
        Object.assign(errors, e.fieldErrors);
      } else {
        formError.value = e.message;
      }
      throw e;
    } finally {
      busy.value = false;
    }
  }

  return { values, errors, formError, busy, reset, submit };
}
