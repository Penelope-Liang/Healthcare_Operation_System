import { describe, expect, test } from "vitest";
import { useForm } from "@/composables/useForm";
import { ApiError } from "@/lib/api";

describe("useForm", () => {
  test("maps 422 field errors onto inputs and keeps the typed values", async () => {
    const form = useForm({ ohip: "", firstName: "" });
    form.values.ohip = "12";
    form.values.firstName = "Jane";

    await expect(form.submit(() => {
      throw new ApiError(422, "Please correct the invalid fields.", { ohip: "OHIP must use the format 0000-000-000." });
    })).rejects.toThrow();

    expect(form.errors.ohip).toBe("OHIP must use the format 0000-000-000.");
    expect(form.formError.value).toBe("");
    expect(form.values.firstName).toBe("Jane");
    expect(form.busy.value).toBe(false);
  });

  test("shows other failures as a form-level message", async () => {
    const form = useForm({ lot: "" });
    await expect(form.submit(() => {
      throw new ApiError(409, "This lot is already shipped to that clinic.");
    })).rejects.toThrow();

    expect(form.formError.value).toBe("This lot is already shipped to that clinic.");
  });

  test("reset restores initial values and clears errors", async () => {
    const form = useForm({ role: "nurse" });
    form.values.role = "doctor";
    await form.submit(() => {
      throw new ApiError(422, "x", { role: "bad" });
    }).catch(() => {});

    form.reset();
    expect(form.values.role).toBe("nurse");
    expect(form.errors.role).toBeUndefined();
  });

  test("passes a copy of the values to the action", async () => {
    const form = useForm({ name: "Rexall" });
    const result = await form.submit((values) => ({ ...values, saved: true }));
    expect(result).toEqual({ name: "Rexall", saved: true });
  });
});
