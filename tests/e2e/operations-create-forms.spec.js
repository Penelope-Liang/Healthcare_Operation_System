const { test, expect } = require("@playwright/test");

test.describe("Operations create forms", () => {
  test("vaccine page exposes lot creation and shipment assignment", async ({ page }) => {
    await page.goto("/vaccines.php");

    await expect(page.getByRole("heading", { name: "Add Vaccine Lot" })).toBeVisible();
    await expect(page.getByLabel("Manufacturer")).toBeVisible();
    await expect(page.getByRole("button", { name: "Create Vaccine Lot" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Assign Shipment" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Assign Shipment" })).toBeVisible();
  });

  test("clinic page exposes clinic creation", async ({ page }) => {
    await page.goto("/clinics.php");

    await expect(page.getByRole("heading", { name: "Add Clinic" })).toBeVisible();
    await expect(page.getByLabel("Clinic Name")).toBeVisible();
    await expect(page.getByRole("button", { name: "Create Clinic" })).toBeVisible();
  });

  test("workers page exposes nurse and doctor assignment creation", async ({ page }) => {
    await page.goto("/workers.php");

    await expect(page.getByRole("heading", { name: "Add Worker Assignment" })).toBeVisible();
    await expect(page.getByLabel("Role")).toBeVisible();
    await expect(page.getByLabel("Credential")).toBeVisible();
    await expect(page.getByRole("button", { name: "Create Worker Assignment" })).toBeVisible();
  });

  test("reports page exposes vaccination record creation", async ({ page }) => {
    await page.goto("/reports.php");

    await expect(page.getByRole("heading", { name: "Add Vaccination Record" })).toBeVisible();
    await expect(page.getByLabel("Patient")).toBeVisible();
    await expect(page.getByLabel("Vaccine Lot")).toBeVisible();
    await expect(page.getByRole("button", { name: "Create Vaccination Record" })).toBeVisible();
  });
});
