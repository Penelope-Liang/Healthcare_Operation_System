const { test, expect } = require("@playwright/test");

test.describe("Database-backed create flows", () => {
  test("supports patient CRUD through the REST API", async ({ request }) => {
    const digits = String(Date.now()).slice(-10);
    const ohip = `${digits.slice(0, 4)}-${digits.slice(4, 7)}-${digits.slice(7, 10)}`;
    const patientUrl = `/api/patients.php?OHIP=${encodeURIComponent(ohip)}`;

    const createResponse = await request.post("/api/patients.php", {
      data: {
        OHIP: ohip,
        FirstName: "Api",
        LastName: "Patient",
        DOB: "1994-04-14"
      }
    });
    const createBody = await createResponse.json();

    expect(createResponse.status()).toBe(201);
    expect(createBody.patient.OHIP).toBe(ohip);

    const readResponse = await request.get(patientUrl);
    const readBody = await readResponse.json();

    expect(readResponse.ok()).toBe(true);
    expect(readBody.patient.FirstName).toBe("Api");

    const updateResponse = await request.put(patientUrl, {
      data: {
        FirstName: "Updated",
        LastName: "Patient",
        DOB: "1994-04-15"
      }
    });
    const updateBody = await updateResponse.json();

    expect(updateResponse.ok()).toBe(true);
    expect(updateBody.patient.FirstName).toBe("Updated");
    expect(updateBody.patient.DOB).toBe("1994-04-15");

    const updatedReadResponse = await request.get(patientUrl);
    const updatedReadBody = await updatedReadResponse.json();

    expect(updatedReadResponse.ok()).toBe(true);
    expect(updatedReadBody.patient.FirstName).toBe("Updated");

    const deleteResponse = await request.delete(patientUrl);
    const deleteBody = await deleteResponse.json();

    expect(deleteResponse.ok()).toBe(true);
    expect(deleteBody.message).toBe("Patient deleted successfully.");

    const deletedReadResponse = await request.get(patientUrl);

    expect(deletedReadResponse.status()).toBe(404);
  });

  test("rejects invalid patient payload through the REST API", async ({ request }) => {
    const response = await request.post("/api/patients.php", {
      data: {
        OHIP: "bad",
        FirstName: "Api1",
        LastName: "",
        DOB: "not-a-date"
      }
    });
    const body = await response.json();

    expect(response.status()).toBe(422);
    expect(body.error).toMatch(/Last name is required|OHIP must use/);
  });

  test("rejects invalid direct POST data on the server", async ({ page }) => {
    const response = await page.request.post("/vaccines.php", {
      form: {
        create_vaccine: "1",
        Lot: "BADSVR",
        CompanyName: "Pfizer",
        Prodcution: "not-a-date",
        Expiry: "2027-08-02",
        Doses: "-1"
      }
    });
    const body = await response.text();

    expect(response.ok()).toBe(true);
    expect(body).toContain("Unable to save vaccine data");
    expect(body).toContain("Production date must use YYYY-MM-DD format.");
  });

  test("creates a patient and shows it in the patient table", async ({ page }) => {
    const digits = String(Date.now()).slice(-10);
    const ohip = `${digits.slice(0, 4)}-${digits.slice(4, 7)}-${digits.slice(7, 10)}`;
    const firstName = "Test";
    const lastName = "Patient";

    await page.goto("/patients.php");
    await page.locator("#OHIP").fill(ohip);
    await page.locator("#FirstName").fill(firstName);
    await page.locator("#LastName").fill(lastName);
    await page.locator("#DOB").fill("1995-05-15");
    await page.getByRole("button", { name: "Create Patient Record" }).click();

    await expect(page.locator(".notice")).toContainText("Patient record created successfully.");
    await page.locator('[data-table-filter="#patients-table"]').fill(ohip);
    await expect(page.locator("#patients-table tbody")).toContainText(ohip);
    await expect(page.locator("#patients-table tbody")).toContainText(`${firstName} ${lastName}`);
  });

  test("creates a clinic and shows it in the clinic directory", async ({ page }) => {
    const suffix = String(Date.now()).slice(-6);
    const clinicName = `QA Clinic ${suffix}`;

    await page.goto("/clinics.php");
    await page.locator("#Name").fill(clinicName);
    await page.locator("#Street").fill("100 Test Street");
    await page.locator("#City").fill("Kingston");
    await page.locator("#Prov").fill("ON");
    await page.locator("#PC").fill("K7L 1A1");
    await page.locator("#date").fill("2026-08-02");
    await page.getByRole("button", { name: "Create Clinic" }).click();

    await expect(page.locator(".notice")).toContainText("Clinic created successfully.");
    await page.locator('[data-table-filter="#clinics-table"]').fill(clinicName);
    await expect(page.locator("#clinics-table tbody")).toContainText(clinicName);
  });

  test("creates a vaccine lot and shows it in inventory", async ({ page }) => {
    const suffix = String(Date.now()).slice(-6);
    const lot = `QA${suffix}`;

    await page.goto("/vaccines.php");
    await page.locator("#Lot").fill(lot);
    await page.locator("#CompanyName").selectOption({ index: 1 });
    await page.locator("#Prodcution").fill("2026-08-02");
    await page.locator("#Expiry").fill("2027-08-02");
    await page.locator("#Doses").fill("25");
    await page.getByRole("button", { name: "Create Vaccine Lot" }).click();

    await expect(page.locator(".notice")).toContainText("Vaccine lot created successfully.");
    await page.locator('[data-table-filter="#vaccines-table"]').fill(lot);
    await expect(page.locator("#vaccines-table tbody")).toContainText(lot);
  });
});
