const { test, expect } = require("@playwright/test");

test.describe("REST API", () => {
  test("health endpoint returns JSON status", async ({ request }) => {
    const response = await request.get("/api/health.php");
    const body = await response.json();

    expect(response.ok()).toBe(true);
    expect(body.status).toBe("ok");
    expect(["connected", "unavailable"]).toContain(body.database);
  });
});
