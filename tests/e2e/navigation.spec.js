const { test, expect } = require("@playwright/test");

const pages = [
  ["Dashboard", "/covid.php", "Dashboard"],
  ["Patients", "/patients.php", "Patient Records"],
  ["Vaccines", "/vaccines.php", "Vaccines"],
  ["Workers", "/workers.php", "Workers"],
  ["Clinics", "/clinics.php", "Clinics"],
  ["Reports", "/reports.php", "Activity Summary"]
];

test.describe("Core navigation", () => {
  for (const [name, path, heading] of pages) {
    test(`${name} page loads`, async ({ page }) => {
      await page.goto(path);
      await expect(page.getByRole("heading", { name: heading }).first()).toBeVisible();
    });
  }

  test("sidebar links navigate between modules", async ({ page }) => {
    await page.goto("/covid.php");
    const sidebar = page.getByRole("navigation", { name: "Primary navigation" });

    await sidebar.getByRole("link", { name: /Patients/ }).click();
    await expect(page).toHaveURL(/patients\.php/);
    await sidebar.getByRole("link", { name: /Reports/ }).click();
    await expect(page).toHaveURL(/reports\.php/);
  });
});
