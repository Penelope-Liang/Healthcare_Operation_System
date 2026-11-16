const { test, expect } = require("@playwright/test");

function uniqueOhip() {
  const digits = String(Date.now()).slice(-10);
  return `${digits.slice(0, 4)}-${digits.slice(4, 7)}-${digits.slice(7, 10)}`;
}

test.describe("End-to-end UI workflows", () => {
  test("register a patient, record two doses, then clean up", async ({ page }) => {
    const ohip = uniqueOhip();
    page.on("dialog", (confirm) => confirm.accept());

    await page.goto("/patients");
    await page.getByRole("button", { name: "Add patient" }).click();
    await page.getByLabel("OHIP number").fill(ohip);
    await page.getByLabel("First name").fill("Grace");
    await page.getByLabel("Last name").fill("Hopper");
    await page.getByLabel("Date of birth").fill("1985-12-09");
    await page.getByRole("button", { name: "Create patient" }).click();

    await expect(page.getByRole("status")).toContainText("Grace Hopper was added.");
    const row = page.locator("#patients-table tbody tr", { hasText: ohip });
    await expect(row.getByText("Not vaccinated")).toBeVisible();

    // Two doses: patients can now have boosters, numbered in date order.
    for (const [date, lot] of [["2026-11-20", "CD2345"], ["2027-02-20", "EF3456"]]) {
      await page.goto("/vaccinations");
      await page.getByRole("button", { name: "Record vaccination" }).click();
      const dialog = page.getByRole("dialog", { name: "Record vaccination" });
      await dialog.getByLabel("Patient").selectOption(ohip);
      await dialog.getByLabel("Clinic").selectOption("Rexall");
      await dialog.getByLabel("Vaccine lot").selectOption(lot);
      await dialog.getByLabel("Date").fill(date);
      await dialog.getByLabel("Time").fill("10:30");
      await dialog.getByRole("button", { name: "Save vaccination" }).click();
      await expect(page.getByRole("status")).toContainText(`Vaccination recorded for ${ohip}.`);
    }
    await expect(page.locator("#vaccinations-table tbody tr", { hasText: ohip })).toHaveCount(2);
    await expect(page.getByRole("button", { name: "Delete dose 2 for Grace Hopper" })).toBeVisible();

    await page.goto("/patients");
    await expect(row.getByText("Vaccinated", { exact: true })).toBeVisible();
    await expect(row.getByText("2 doses")).toBeVisible();

    // A patient with vaccination history cannot be deleted until the records are removed.
    await row.getByRole("button", { name: "Delete Grace Hopper" }).click();
    await expect(page.getByRole("alert")).toContainText("still has vaccination records");

    await page.goto("/vaccinations");
    await page.getByRole("button", { name: "Delete dose 2 for Grace Hopper" }).click();
    await expect(page.getByRole("status")).toContainText("Dose 2 for Grace Hopper was removed.");
    await page.getByRole("button", { name: "Delete dose 1 for Grace Hopper" }).click();
    await expect(page.locator("#vaccinations-table tbody tr", { hasText: ohip })).toHaveCount(0);
    await page.goto("/patients");
    await page.locator("#patients-table tbody tr", { hasText: ohip })
      .getByRole("button", { name: "Delete Grace Hopper" }).click();
    await expect(page.getByRole("status")).toContainText("was removed");
    await expect(page.locator("#patients-table tbody tr", { hasText: ohip })).toHaveCount(0);
  });

  test("staff role filter narrows the table", async ({ page }) => {
    await page.goto("/staff");
    await page.getByRole("link", { name: /Doctors/ }).click();
    await expect(page).toHaveURL(/role=doctor/);
    await expect(page.locator("#staff-table tbody").getByText("Nurse", { exact: true })).toHaveCount(0);
    await expect(page.locator("#staff-table tbody").getByText("Doctor", { exact: true }).first()).toBeVisible();
  });
});
