import { afterEach, describe, expect, test, vi } from "vitest";
import { api, ApiError } from "@/lib/api";

function mockFetch(status, body) {
  global.fetch = vi.fn().mockResolvedValue({
    ok: status >= 200 && status < 300,
    status,
    json: () => (body === undefined ? Promise.reject(new Error("no body")) : Promise.resolve(body))
  });
}

afterEach(() => {
  vi.restoreAllMocks();
});

describe("api client", () => {
  test("sends JSON and returns the parsed body", async () => {
    mockFetch(201, { ohip: "1234-567-890" });
    const result = await api.createPatient({ ohip: "1234-567-890" });

    expect(result.ohip).toBe("1234-567-890");
    const [url, init] = global.fetch.mock.calls[0];
    expect(url).toBe("/api/patients");
    expect(init.method).toBe("POST");
    expect(JSON.parse(init.body)).toEqual({ ohip: "1234-567-890" });
  });

  test("encodes path segments such as clinic names", async () => {
    mockFetch(204);
    await api.deleteShipment("AB1234", "Shoppers Drug Mart");
    expect(global.fetch.mock.calls[0][0]).toBe("/api/shipments/AB1234/Shoppers%20Drug%20Mart");
  });

  test("turns error responses into ApiError with field errors", async () => {
    mockFetch(422, { error: "Please correct the invalid fields.", fieldErrors: { ohip: "Bad format" } });
    const error = await api.createPatient({}).catch((e) => e);

    expect(error).toBeInstanceOf(ApiError);
    expect(error.status).toBe(422);
    expect(error.fieldErrors.ohip).toBe("Bad format");
  });

  test("flags 503 as a database outage", async () => {
    mockFetch(503, { error: "Database is not connected." });
    const error = await api.dashboard().catch((e) => e);
    expect(error.databaseDown).toBe(true);
  });

  test("reports network failures clearly", async () => {
    global.fetch = vi.fn().mockRejectedValue(new TypeError("Failed to fetch"));
    const error = await api.dashboard().catch((e) => e);
    expect(error.status).toBe(0);
    expect(error.message).toContain("Can't reach the server");
  });
});
