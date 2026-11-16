const { test, expect } = require("@playwright/test");

function uniqueSuffix(length) {
  return String(Date.now()).slice(-length);
}

function uniqueOhip() {
  const d = uniqueSuffix(10);
  return `${d.slice(0, 4)}-${d.slice(4, 7)}-${d.slice(7, 10)}`;
}

test.describe("REST API", () => {
  test("health endpoint returns JSON status", async ({ request }) => {
    const response = await request.get("/api/health");
    const body = await response.json();

    expect(response.ok()).toBe(true);
    expect(body.status).toBe("ok");
    expect(["connected", "unavailable"]).toContain(body.database);
  });

  test("patient CRUD", async ({ request }) => {
    const ohip = uniqueOhip();

    const created = await request.post("/api/patients", {
      data: { ohip, firstName: "Api", lastName: "O'Patient", dateOfBirth: "1994-04-14" }
    });
    expect(created.status()).toBe(201);
    expect(created.headers()["location"]).toContain(`/api/patients/${ohip}`);

    const read = await request.get(`/api/patients/${ohip}`);
    expect((await read.json()).lastName).toBe("O'Patient");

    const updated = await request.put(`/api/patients/${ohip}`, {
      data: { firstName: "Updated", lastName: "Patient", dateOfBirth: "1994-04-15" }
    });
    expect(updated.ok()).toBe(true);
    expect((await updated.json()).dateOfBirth).toBe("1994-04-15");

    expect((await request.delete(`/api/patients/${ohip}`)).status()).toBe(204);
    const gone = await request.get(`/api/patients/${ohip}`);
    expect(gone.status()).toBe(404);
    expect((await gone.json()).error).toContain("not found");
  });

  test("validation errors are reported per field", async ({ request }) => {
    const response = await request.post("/api/patients", { data: { ohip: "123", firstName: "Jane1" } });
    const body = await response.json();

    expect(response.status()).toBe(422);
    expect(body.fieldErrors.ohip).toBe("OHIP must use the format 0000-000-000.");
    expect(body.fieldErrors.firstName).toContain("First name must be 1-30 letters");
    expect(body.fieldErrors.lastName).toBe("Last name is required.");
    expect(body.fieldErrors.dateOfBirth).toBe("Date of birth is required.");
  });

  test("malformed JSON is rejected", async ({ request }) => {
    const response = await request.post("/api/patients", {
      headers: { "Content-Type": "application/json" },
      data: "{not json"
    });
    expect(response.status()).toBe(400);
  });

  test("clinic, vaccine lot, shipment, vaccination and staff lifecycle", async ({ request }) => {
    const suffix = uniqueSuffix(4);
    const clinicName = `Api Clinic ${suffix}`;
    const lot = `Z${suffix}`;
    const ohip = uniqueOhip();
    const staffId = `W${suffix}`;

    expect((await request.post("/api/clinics", {
      data: { name: clinicName, street: "100 Api Street", city: "Kingston", province: "ON", postalCode: "K7L 1A1", operatingDate: "2026-08-02" }
    })).status()).toBe(201);
    expect((await request.post("/api/vaccines", {
      data: { lot, manufacturer: "Pfizer", productionDate: "2026-08-02", expiryDate: "2027-08-02", doses: 20 }
    })).status()).toBe(201);
    expect((await request.post("/api/shipments", { data: { lot, clinicName } })).status()).toBe(201);
    expect((await request.post("/api/shipments", { data: { lot, clinicName } })).status()).toBe(409);

    expect((await request.post("/api/patients", {
      data: { ohip, firstName: "Shot", lastName: "Taker", dateOfBirth: "1980-01-01" }
    })).status()).toBe(201);
    const doseIds = [];
    for (const date of ["2026-08-03", "2026-11-03"]) {
      const dose = await request.post("/api/vaccinations", { data: { ohip, clinicName, lot, date, time: "09:30" } });
      expect(dose.status()).toBe(201);
      doseIds.push((await dose.json()).id);
    }

    const member = await request.post("/api/staff", {
      data: { id: staffId, role: "nurse", firstName: "Api", lastName: "Nurse", credentials: ["RN"], clinics: [clinicName] }
    });
    expect(member.status()).toBe(201);
    const updatedMember = await request.put(`/api/staff/${staffId}`, {
      data: { role: "nurse", firstName: "Api", lastName: "Nurse", credentials: ["RN", "BSN"], clinics: [clinicName] }
    });
    expect((await updatedMember.json()).credentials).toEqual(["RN", "BSN"]);

    // The clinic is still referenced by vaccination records.
    expect((await request.delete(`/api/clinics/${encodeURIComponent(clinicName)}`)).status()).toBe(409);

    expect((await request.delete(`/api/staff/${staffId}`)).status()).toBe(204);
    for (const id of doseIds) {
      expect((await request.delete(`/api/vaccinations/${id}`)).status()).toBe(204);
    }
    expect((await request.delete(`/api/patients/${ohip}`)).status()).toBe(204);
    expect((await request.delete(`/api/shipments/${lot}/${encodeURIComponent(clinicName)}`)).status()).toBe(204);
    expect((await request.delete(`/api/vaccines/${lot}`)).status()).toBe(204);
    expect((await request.delete(`/api/clinics/${encodeURIComponent(clinicName)}`)).status()).toBe(204);
  });
});
