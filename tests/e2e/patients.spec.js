const { test, expect } = require("@playwright/test");

test.describe("Patient management", () => {
  test("patient form requires valid OHIP format", async ({ page }) => {
    await page.goto("/patients.php");
    const ohip = page.locator("#OHIP");

    await ohip.fill("123");
    await page.getByRole("button", { name: "Create Patient Record" }).click();

    await expect(ohip).toHaveJSProperty("validity.patternMismatch", true);
  });

  test("patient table filter is wired to the records table", async ({ page }) => {
    await page.goto("/patients.php");

    await expect(page.locator('[data-table-filter="#patients-table"]')).toBeVisible();
    await expect(page.locator("#patients-table")).toBeVisible();
  });
});
