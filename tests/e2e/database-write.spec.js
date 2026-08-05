const { test, expect } = require("@playwright/test");

function uniqueSuffix(length = 6) {
  return String(Date.now()).slice(-length);
}

function uniqueOhip() {
  const digits = uniqueSuffix(10);
  return `${digits.slice(0, 4)}-${digits.slice(4, 7)}-${digits.slice(7, 10)}`;
}

function apiUrl(path, params) {
  return `${path}?${new URLSearchParams(params).toString()}`;
}

async function createClinic(request, name, overrides = {}) {
  return request.post("/api/clinics.php", {
    data: {
      Name: name,
      Street: "100 Api Street",
      City: "Kingston",
      Prov: "ON",
      PC: "K7L 1A1",
      date: "2026-08-02",
      ...overrides
    }
  });
}

async function createVaccine(request, lot, overrides = {}) {
  return request.post("/api/vaccines.php", {
    data: {
      Lot: lot,
      CompanyName: "Pfizer",
      Prodcution: "2026-08-02",
      Expiry: "2027-08-02",
      Doses: 20,
      ...overrides
    }
  });
}

test.describe("Database-backed create flows", () => {
  test("supports patient CRUD through the REST API", async ({ request }) => {
    const ohip = uniqueOhip();
    const patientUrl = apiUrl("/api/patients.php", { OHIP: ohip });

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

  test("supports clinic CRUD through the REST API", async ({ request }) => {
    const clinicName = `API Clinic ${uniqueSuffix()}`;
    const clinicUrl = apiUrl("/api/clinics.php", { Name: clinicName });

    const createResponse = await createClinic(request, clinicName);
    const createBody = await createResponse.json();

    expect(createResponse.status()).toBe(201);
    expect(createBody.clinic.Name).toBe(clinicName);

    const updateResponse = await request.put(clinicUrl, {
      data: {
        Street: "200 Api Street",
        City: "Kingston",
        Prov: "ON",
        PC: "K7L 2B2",
        date: "2026-08-03"
      }
    });
    const updateBody = await updateResponse.json();

    expect(updateResponse.ok()).toBe(true);
    expect(updateBody.clinic.Street).toBe("200 Api Street");

    const readResponse = await request.get(clinicUrl);
    const readBody = await readResponse.json();

    expect(readResponse.ok()).toBe(true);
    expect(readBody.clinic.PC).toBe("K7L 2B2");

    const deleteResponse = await request.delete(clinicUrl);

    expect(deleteResponse.ok()).toBe(true);
    expect((await request.get(clinicUrl)).status()).toBe(404);
  });

  test("supports vaccine lot CRUD through the REST API", async ({ request }) => {
    const lot = `V${uniqueSuffix(5)}`;
    const vaccineUrl = apiUrl("/api/vaccines.php", { Lot: lot });

    const createResponse = await createVaccine(request, lot);
    const createBody = await createResponse.json();

    expect(createResponse.status()).toBe(201);
    expect(createBody.vaccine.Lot).toBe(lot);

    const updateResponse = await request.put(vaccineUrl, {
      data: {
        CompanyName: "Moderna",
        Prodcution: "2026-08-03",
        Expiry: "2027-08-03",
        Doses: 30
      }
    });
    const updateBody = await updateResponse.json();

    expect(updateResponse.ok()).toBe(true);
    expect(updateBody.vaccine.Doses).toBe(30);

    const readResponse = await request.get(vaccineUrl);
    const readBody = await readResponse.json();

    expect(readResponse.ok()).toBe(true);
    expect(Number(readBody.vaccine.Doses)).toBe(30);

    const deleteResponse = await request.delete(vaccineUrl);

    expect(deleteResponse.ok()).toBe(true);
    expect((await request.get(vaccineUrl)).status()).toBe(404);
  });

  test("supports shipment create/read/delete through the REST API", async ({ request }) => {
    const suffix = uniqueSuffix(5);
    const lot = `S${suffix}`;
    const clinicName = `Ship Clinic ${suffix}`;
    const shipmentUrl = apiUrl("/api/shipments.php", { Lots: lot, Clinic: clinicName });

    await createVaccine(request, lot, { Doses: 15 });
    await createClinic(request, clinicName, { Street: "300 Ship Street", PC: "K7L 3C3" });

    const createResponse = await request.post("/api/shipments.php", {
      data: {
        Lots: lot,
        Clinic: clinicName
      }
    });
    const createBody = await createResponse.json();

    expect(createResponse.status()).toBe(201);
    expect(createBody.shipment.Lots).toBe(lot);

    const readResponse = await request.get(shipmentUrl);
    const readBody = await readResponse.json();

    expect(readResponse.ok()).toBe(true);
    expect(readBody.shipment.Clinic).toBe(clinicName);

    expect((await request.delete(shipmentUrl)).ok()).toBe(true);
    expect((await request.get(shipmentUrl)).status()).toBe(404);

    await request.delete(apiUrl("/api/vaccines.php", { Lot: lot }));
    await request.delete(apiUrl("/api/clinics.php", { Name: clinicName }));
  });

  test("supports worker CRUD through the REST API", async ({ request }) => {
    const suffix = uniqueSuffix(4);
    const workerId = `N${suffix}`;
    const workerUrl = apiUrl("/api/workers.php", { Role: "nurse", Id: workerId });

    const createResponse = await request.post("/api/workers.php", {
      data: {
        Role: "nurse",
        Id: workerId,
        FirstName: "Api",
        LastName: "Nurse",
        Credential: "RN",
        VaxClinicName: "Rexall"
      }
    });
    const createBody = await createResponse.json();

    expect(createResponse.status()).toBe(201);
    expect(createBody.worker.Id).toBe(workerId);

    const updateResponse = await request.put(workerUrl, {
      data: {
        FirstName: "Updated",
        LastName: "Nurse",
        Credential: "BSN",
        VaxClinicName: "Shoppers Drug Mart"
      }
    });
    const updateBody = await updateResponse.json();

    expect(updateResponse.ok()).toBe(true);
    expect(updateBody.worker.Credential).toBe("BSN");

    const readResponse = await request.get(workerUrl);
    const readBody = await readResponse.json();

    expect(readResponse.ok()).toBe(true);
    expect(readBody.worker.FirstName).toBe("Updated");

    expect((await request.delete(workerUrl)).ok()).toBe(true);
    expect((await request.get(workerUrl)).status()).toBe(404);
  });

  test("supports vaccination record CRUD through the REST API", async ({ request }) => {
    const suffix = uniqueSuffix(5);
    const ohip = uniqueOhip();
    const lot = `R${suffix}`;
    const clinicName = `Vax Clinic ${suffix}`;
    const vaccinationUrl = apiUrl("/api/vaccinations.php", { OHIP: ohip });

    await request.post("/api/patients.php", {
      data: {
        OHIP: ohip,
        FirstName: "Vax",
        LastName: "Patient",
        DOB: "1990-01-01"
      }
    });
    await createVaccine(request, lot, { Doses: 10 });
    await createClinic(request, clinicName, { Street: "400 Vax Street", PC: "K7L 4D4" });

    const createResponse = await request.post("/api/vaccinations.php", {
      data: {
        OHIP: ohip,
        ClinicName: clinicName,
        Lots: lot,
        Date: "2026-08-04",
        Time: "10:30"
      }
    });
    const createBody = await createResponse.json();

    expect(createResponse.status()).toBe(201);
    expect(createBody.vaccination.OHIP).toBe(ohip);

    const updateResponse = await request.put(vaccinationUrl, {
      data: {
        ClinicName: clinicName,
        Lots: lot,
        Date: "2026-08-05",
        Time: "11:45"
      }
    });
    const updateBody = await updateResponse.json();

    expect(updateResponse.ok()).toBe(true);
    expect(updateBody.vaccination.Time).toBe("11:45");

    const readResponse = await request.get(vaccinationUrl);
    const readBody = await readResponse.json();

    expect(readResponse.ok()).toBe(true);
    expect(readBody.vaccination.Date).toBe("2026-08-05");

    expect((await request.delete(vaccinationUrl)).ok()).toBe(true);
    expect((await request.get(vaccinationUrl)).status()).toBe(404);

    await request.delete(apiUrl("/api/patients.php", { OHIP: ohip }));
    await request.delete(apiUrl("/api/vaccines.php", { Lot: lot }));
    await request.delete(apiUrl("/api/clinics.php", { Name: clinicName }));
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
    const ohip = uniqueOhip();
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
    const suffix = uniqueSuffix();
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
    const suffix = uniqueSuffix();
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
