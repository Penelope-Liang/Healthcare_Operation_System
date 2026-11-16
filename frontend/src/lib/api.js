/** Thin fetch wrapper for the Spring Boot REST API. Errors carry the API's status and field errors. */
export class ApiError extends Error {
  constructor(status, message, fieldErrors = {}) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.fieldErrors = fieldErrors;
  }

  get databaseDown() {
    return this.status === 503;
  }
}

async function request(method, url, body) {
  let response;
  try {
    response = await fetch(url, {
      method,
      headers: body === undefined ? { Accept: "application/json" } : { "Content-Type": "application/json", Accept: "application/json" },
      body: body === undefined ? undefined : JSON.stringify(body)
    });
  } catch (cause) {
    throw new ApiError(0, "Can't reach the server. Is the Spring Boot app running?");
  }

  if (response.status === 204) {
    return null;
  }
  const payload = await response.json().catch(() => null);
  if (!response.ok) {
    throw new ApiError(response.status, payload?.error || `Request failed (${response.status}).`, payload?.fieldErrors || {});
  }
  return payload;
}

const enc = encodeURIComponent;

export const api = {
  get: (url) => request("GET", url),
  post: (url, body) => request("POST", url, body),
  put: (url, body) => request("PUT", url, body),
  del: (url) => request("DELETE", url),

  dashboard: () => request("GET", "/api/dashboard"),
  patientOverview: () => request("GET", "/api/patients/overview"),
  patients: () => request("GET", "/api/patients"),
  createPatient: (p) => request("POST", "/api/patients", p),
  deletePatient: (ohip) => request("DELETE", `/api/patients/${enc(ohip)}`),

  vaccinationDetails: () => request("GET", "/api/vaccinations/details"),
  createVaccination: (v) => request("POST", "/api/vaccinations", v),
  deleteVaccination: (id) => request("DELETE", `/api/vaccinations/${enc(id)}`),

  vaccineLots: () => request("GET", "/api/vaccines"),
  companies: () => request("GET", "/api/companies"),
  createVaccineLot: (v) => request("POST", "/api/vaccines", v),
  deleteVaccineLot: (lot) => request("DELETE", `/api/vaccines/${enc(lot)}`),

  shipments: () => request("GET", "/api/shipments"),
  createShipment: (s) => request("POST", "/api/shipments", s),
  deleteShipment: (lot, clinic) => request("DELETE", `/api/shipments/${enc(lot)}/${enc(clinic)}`),

  clinics: () => request("GET", "/api/clinics"),
  clinicOverview: () => request("GET", "/api/clinics/overview"),
  createClinic: (c) => request("POST", "/api/clinics", c),
  deleteClinic: (name) => request("DELETE", `/api/clinics/${enc(name)}`),

  staff: () => request("GET", "/api/staff"),
  createStaff: (m) => request("POST", "/api/staff", m),
  deleteStaff: (id) => request("DELETE", `/api/staff/${enc(id)}`)
};
