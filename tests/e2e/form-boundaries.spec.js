const { test, expect } = require("@playwright/test");

async function expectInvalidAfterSubmit(page, buttonName, locator, property) {
  await page.getByRole("button", { name: buttonName }).click();
  await expect(locator).toHaveJSProperty(property, true);
}

test.describe("Form boundary validation", () => {
  test("patient form enforces required fields and name pattern", async ({ page }) => {
    await page.goto("/patients.php");

    await expectInvalidAfterSubmit(
      page,
      "Create Patient Record",
      page.locator("#OHIP"),
      "validity.valueMissing"
    );

    await page.locator("#OHIP").fill("9999-888-777");
    await expectInvalidAfterSubmit(
      page,
      "Create Patient Record",
      page.locator("#FirstName"),
      "validity.valueMissing"
    );

    await page.locator("#FirstName").fill("Jane1");
    await expect(page.locator("#FirstName")).toHaveJSProperty("validity.patternMismatch", true);

    await page.locator("#FirstName").fill("Jane");
    await page.locator("#LastName").fill("Doe1");
    await expect(page.locator("#LastName")).toHaveJSProperty("validity.patternMismatch", true);
  });

  test("vaccine lot form blocks missing lot and negative dose count", async ({ page }) => {
    await page.goto("/vaccines.php");

    await expectInvalidAfterSubmit(
      page,
      "Create Vaccine Lot",
      page.locator("#Lot"),
      "validity.valueMissing"
    );

    await page.locator("#Lot").fill("ZZ9999");
    await page.locator("#Doses").fill("-1");
    await expect(page.locator("#Doses")).toHaveJSProperty("validity.rangeUnderflow", true);
  });

  test("clinic form requires address and operating date fields", async ({ page }) => {
    await page.goto("/clinics.php");

    await expectInvalidAfterSubmit(
      page,
      "Create Clinic",
      page.locator("#Name"),
      "validity.valueMissing"
    );

    await page.locator("#Name").fill("Boundary Clinic");
    await page.locator("#Street").fill("100 Test St");
    await page.locator("#City").fill("Kingston");
    await page.locator("#Prov").fill("ON");
    await page.locator("#PC").fill("K7L 1A1");
    await expectInvalidAfterSubmit(
      page,
      "Create Clinic",
      page.locator("#date"),
      "validity.valueMissing"
    );
  });

  test("worker form requires role, worker identity, credential, and clinic", async ({ page }) => {
    await page.goto("/workers.php");

    await expectInvalidAfterSubmit(
      page,
      "Create Worker Assignment",
      page.locator("#Role"),
      "validity.valueMissing"
    );

    await page.locator("#Role").selectOption("nurse");
    await expectInvalidAfterSubmit(
      page,
      "Create Worker Assignment",
      page.locator("#Id"),
      "validity.valueMissing"
    );

    await page.locator("#Id").fill("QA123");
    await page.locator("#FirstName").fill("Nina");
    await page.locator("#LastName").fill("Patel");
    await expectInvalidAfterSubmit(
      page,
      "Create Worker Assignment",
      page.locator("#Credential"),
      "validity.valueMissing"
    );
  });

  test("vaccination record form requires patient, clinic, lot, date, and time", async ({ page }) => {
    await page.goto("/reports.php");

    await expectInvalidAfterSubmit(
      page,
      "Create Vaccination Record",
      page.locator("#OHIP"),
      "validity.valueMissing"
    );

    await expect(page.locator("#Date")).toHaveJSProperty("validity.valueMissing", true);
    await expect(page.locator("#Time")).toHaveJSProperty("validity.valueMissing", true);
  });
});
