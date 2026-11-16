const { test, expect } = require("@playwright/test");

async function openDialog(page, path, opener, dialogName) {
  await page.goto(path);
  await page.getByRole("button", { name: opener, exact: true }).first().click();
  const dialog = page.getByRole("dialog", { name: dialogName });
  await expect(dialog).toBeVisible();
  return dialog;
}

async function expectInvalidAfterSubmit(dialog, buttonName, locator, property) {
  await dialog.getByRole("button", { name: buttonName }).click();
  await expect(locator).toHaveJSProperty(property, true);
}

test.describe("Form boundary validation", () => {
  test("patient form enforces required fields and name pattern", async ({ page }) => {
    const dialog = await openDialog(page, "/patients", "Add patient", "Add patient");

    await expectInvalidAfterSubmit(dialog, "Create patient", dialog.getByLabel("OHIP number"), "validity.valueMissing");

    await dialog.getByLabel("OHIP number").fill("9999-888-777");
    await expectInvalidAfterSubmit(dialog, "Create patient", dialog.getByLabel("First name"), "validity.valueMissing");

    await dialog.getByLabel("First name").fill("Jane1");
    await expect(dialog.getByLabel("First name")).toHaveJSProperty("validity.patternMismatch", true);

    await dialog.getByLabel("First name").fill("Jane");
    await dialog.getByLabel("Last name").fill("Doe1");
    await expect(dialog.getByLabel("Last name")).toHaveJSProperty("validity.patternMismatch", true);
  });

  test("vaccine lot form blocks missing lot, long lot codes and negative doses", async ({ page }) => {
    const dialog = await openDialog(page, "/vaccines", "Add lot", "Add vaccine lot");

    await expectInvalidAfterSubmit(dialog, "Create lot", dialog.getByLabel("Lot code"), "validity.valueMissing");
    await expect(dialog.getByLabel("Lot code")).toHaveAttribute("maxlength", "12");

    await dialog.getByLabel("Lot code").fill("ZZ99!");
    await expect(dialog.getByLabel("Lot code")).toHaveJSProperty("validity.patternMismatch", true);

    await dialog.getByLabel("Doses").fill("-1");
    await expect(dialog.getByLabel("Doses")).toHaveJSProperty("validity.rangeUnderflow", true);
  });

  test("shipment form requires a lot and a clinic", async ({ page }) => {
    const dialog = await openDialog(page, "/vaccines", "Assign shipment", "Assign shipment");
    await expectInvalidAfterSubmit(dialog, "Assign shipment", dialog.getByLabel("Vaccine lot"), "validity.valueMissing");
    await expect(dialog.getByLabel("Destination clinic")).toHaveJSProperty("validity.valueMissing", true);
  });

  test("clinic form requires address and operating date fields", async ({ page }) => {
    const dialog = await openDialog(page, "/clinics", "Add clinic", "Add clinic");

    await expectInvalidAfterSubmit(dialog, "Create clinic", dialog.getByLabel("Clinic name"), "validity.valueMissing");

    await dialog.getByLabel("Clinic name").fill("Boundary Clinic");
    await dialog.getByLabel("Street").fill("100 Test St");
    await dialog.getByLabel("City").fill("Kingston");
    await dialog.getByLabel("Province").fill("ON");
    await dialog.getByLabel("Postal code").fill("K7L 1A1");
    await expectInvalidAfterSubmit(dialog, "Create clinic", dialog.getByLabel("Operating date"), "validity.valueMissing");
  });

  test("staff form requires identity, credential and clinic", async ({ page }) => {
    const dialog = await openDialog(page, "/staff", "Add staff member", "Add staff member");

    await expect(dialog.getByLabel("Role")).toHaveValue("nurse");
    await expectInvalidAfterSubmit(dialog, "Save staff member", dialog.getByLabel("Staff ID"), "validity.valueMissing");

    await dialog.getByLabel("Staff ID").fill("QA1234");
    await expect(dialog.getByLabel("Staff ID")).toHaveValue("QA123");
    await dialog.getByLabel("First name").fill("Nina");
    await dialog.getByLabel("Last name").fill("Patel");
    await expectInvalidAfterSubmit(dialog, "Save staff member", dialog.getByLabel("Credential"), "validity.valueMissing");
  });

  test("vaccination form requires patient, clinic, lot, date and time", async ({ page }) => {
    const dialog = await openDialog(page, "/vaccinations", "Record vaccination", "Record vaccination");

    await expectInvalidAfterSubmit(dialog, "Save vaccination", dialog.getByLabel("Patient"), "validity.valueMissing");
    await expect(dialog.getByLabel("Date")).toHaveJSProperty("validity.valueMissing", true);
    await expect(dialog.getByLabel("Time")).toHaveJSProperty("validity.valueMissing", true);
  });
});
