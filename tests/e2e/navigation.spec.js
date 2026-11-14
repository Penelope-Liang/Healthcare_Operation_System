const { test, expect } = require("@playwright/test");

const pages = [
  ["Dashboard", "/", "Dashboard"],
  ["Patients", "/patients", "Patients"],
  ["Vaccinations", "/vaccinations", "Vaccinations"],
  ["Vaccine inventory", "/vaccines", "Vaccine inventory"],
  ["Clinics", "/clinics", "Clinics"],
  ["Clinical staff", "/staff", "Clinical staff"]
];

test.describe("Core navigation", () => {
  for (const [name, path, heading] of pages) {
    test(`${name} page loads`, async ({ page }) => {
      await page.goto(path);
      await expect(page.getByRole("heading", { level: 1, name: heading })).toBeVisible();
      await expect(page).toHaveTitle(new RegExp(`${heading} · HealthOps`));
    });
  }

  test("sidebar links navigate between modules and mark the current page", async ({ page }) => {
    await page.goto("/");
    const sidebar = page.getByRole("navigation", { name: "Primary navigation" });

    await sidebar.getByRole("link", { name: "Patients" }).click();
    await expect(page).toHaveURL(/\/patients$/);
    await expect(sidebar.getByRole("link", { name: "Patients" })).toHaveAttribute("aria-current", "page");

    await sidebar.getByRole("link", { name: "Clinical staff" }).click();
    await expect(page).toHaveURL(/\/staff$/);
    await sidebar.getByRole("link", { name: "Dashboard" }).click();
    await expect(page).toHaveURL(/\/$/);
  });

  test("dashboard shows metrics and recent activity", async ({ page }) => {
    await page.goto("/");
    const metrics = page.getByRole("region", { name: "Key metrics" });
    await expect(metrics.getByText("Registered patients")).toBeVisible();
    await expect(metrics.getByText("Vaccination coverage")).toBeVisible();
    await expect(page.getByRole("heading", { name: "Recent vaccinations" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Clinic coverage" })).toBeVisible();
  });

  test("layout has no horizontal scroll on a phone", async ({ page }) => {
    await page.setViewportSize({ width: 390, height: 844 });
    for (const [, path] of pages) {
      await page.goto(path);
      const width = await page.evaluate(() => document.documentElement.scrollWidth);
      expect(width, path).toBeLessThanOrEqual(390);
    }
  });
});
