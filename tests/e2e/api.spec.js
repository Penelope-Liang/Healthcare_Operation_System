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
      data: { ohip, firstName: "Api", lastName: "Patient", dob: "1994-04-14" }
    });
    expect(created.status()).toBe(201);
    expect(created.headers()["location"]).toContain(`/api/patients/${ohip}`);

    const read = await request.get(`/api/patients/${ohip}`);
    expect((await read.json()).firstName).toBe("Api");

    const updated = await request.put(`/api/patients/${ohip}`, {
      data: { firstName: "Updated", lastName: "Patient", dob: "1994-04-15" }
    });
    expect(updated.ok()).toBe(true);
    expect((await updated.json()).dob).toBe("1994-04-15");

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
    expect(body.fieldErrors.firstName).toBe("First name must be 1-30 letters.");
    expect(body.fieldErrors.lastName).toBe("Last name is required.");
    expect(body.fieldErrors.dob).toBe("Date of birth is required.");
  });

  test("malformed JSON is rejected", async ({ request }) => {
    const response = await request.post("/api/patients", {
      headers: { "Content-Type": "application/json" },
      data: "{not json"
    });
    expect(response.status()).toBe(400);
  });

  test("clinic, vaccine lot, shipment, vaccination and worker lifecycle", async ({ request }) => {
    const suffix = uniqueSuffix(4);
    const clinic = `Api Clinic ${suffix}`;
    const lot = `Z${suffix}`;
    const ohip = uniqueOhip();
    const workerId = `W${suffix}`;

    expect((await request.post("/api/clinics", {
      data: { name: clinic, street: "100 Api Street", city: "Kingston", prov: "ON", pc: "K7L 1A1", operatingDate: "2026-08-02" }
    })).status()).toBe(201);
    expect((await request.post("/api/vaccines", {
      data: { lot, companyName: "Pfizer", production: "2026-08-02", expiry: "2027-08-02", doses: 20 }
    })).status()).toBe(201);
    expect((await request.post("/api/shipments", { data: { lot, clinic } })).status()).toBe(201);
    expect((await request.post("/api/shipments", { data: { lot, clinic } })).status()).toBe(409);

    expect((await request.post("/api/patients", {
      data: { ohip, firstName: "Shot", lastName: "Taker", dob: "1980-01-01" }
    })).status()).toBe(201);
    expect((await request.post("/api/vaccinations", {
      data: { ohip, clinicName: clinic, lot, date: "2026-08-03", time: "09:30" }
    })).status()).toBe(201);

    const worker = await request.post("/api/workers", {
      data: { role: "nurse", id: workerId, firstName: "Api", lastName: "Nurse", credential: "RN", clinicName: clinic }
    });
    expect(worker.status()).toBe(201);
    const updatedWorker = await request.put(`/api/workers/nurse/${workerId}`, {
      data: { firstName: "Api", lastName: "Nurse", credential: "BSN", clinicName: clinic }
    });
    expect((await updatedWorker.json()).credential).toBe("BSN");

    // The clinic is still referenced by the vaccination record.
    expect((await request.delete(`/api/clinics/${encodeURIComponent(clinic)}`)).status()).toBe(409);

    expect((await request.delete(`/api/workers/nurse/${workerId}`)).status()).toBe(204);
    expect((await request.delete(`/api/vaccinations/${ohip}`)).status()).toBe(204);
    expect((await request.delete(`/api/patients/${ohip}`)).status()).toBe(204);
    expect((await request.delete(`/api/shipments/${lot}/${encodeURIComponent(clinic)}`)).status()).toBe(204);
    expect((await request.delete(`/api/vaccines/${lot}`)).status()).toBe(204);
    expect((await request.delete(`/api/clinics/${encodeURIComponent(clinic)}`)).status()).toBe(204);
  });
});
