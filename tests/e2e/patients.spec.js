const { test, expect } = require("@playwright/test");

test.describe("Patient management", () => {
  test("add patient dialog opens and closes", async ({ page }) => {
    await page.goto("/patients");
    const dialog = page.getByRole("dialog", { name: "Add patient" });

    await expect(dialog).toBeHidden();
    await page.getByRole("button", { name: "Add patient" }).click();
    await expect(dialog).toBeVisible();
    await dialog.getByRole("button", { name: "Cancel" }).click();
    await expect(dialog).toBeHidden();
  });

  test("patient form requires a valid OHIP format", async ({ page }) => {
    await page.goto("/patients");
    await page.getByRole("button", { name: "Add patient" }).click();
    const ohip = page.getByLabel("OHIP number");

    await ohip.fill("123");
    await page.getByRole("button", { name: "Create patient" }).click();

    await expect(ohip).toHaveJSProperty("validity.patternMismatch", true);
  });

  test("search filters the patient directory", async ({ page }) => {
    await page.goto("/patients");
    const rows = page.locator("#patients-table tbody tr");
    const search = page.getByRole("searchbox", { name: "Search patients" });

    await search.fill("Amy");
    await expect(rows.filter({ hasText: "Amy White" })).toBeVisible();
    await expect(rows.filter({ hasText: "John Smith" })).toBeHidden();

    await search.fill("zzz-no-match");
    await expect(page.getByText("No matching patients")).toBeVisible();

    await search.fill("");
    await expect(rows.filter({ hasText: "John Smith" })).toBeVisible();
  });

  test("server-side validation re-opens the dialog with field errors", async ({ page }) => {
    await page.goto("/patients");
    await page.getByRole("button", { name: "Add patient" }).click();
    const dialog = page.getByRole("dialog", { name: "Add patient" });

    // Bypass the browser checks to exercise the server's Bean Validation.
    await dialog.locator("form").evaluate((form) => form.setAttribute("novalidate", ""));
    await page.getByLabel("OHIP number").fill("12");
    await page.getByLabel("First name").fill("Jane");
    await page.getByLabel("Last name").fill("Doe");
    await page.getByLabel("Date of birth").fill("1990-01-01");
    await page.getByRole("button", { name: "Create patient" }).click();

    await expect(dialog).toBeVisible();
    await expect(dialog.getByText("OHIP must use the format 0000-000-000.")).toBeVisible();
    await expect(page.getByLabel("First name")).toHaveValue("Jane");
  });
});
