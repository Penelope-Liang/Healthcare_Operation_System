const { test, expect } = require("@playwright/test");

test.describe("Database error handling", () => {
  test("pages render instead of blanking when MySQL is unavailable", async ({ page }) => {
    await page.goto("/covid.php");

    await expect(page.getByRole("heading", { name: "Dashboard" })).toBeVisible();
    const possibleError = page.locator(".notice.error");
    if (await possibleError.count()) {
      await expect(possibleError.first()).toContainText(/Database/);
    }
  });
});
