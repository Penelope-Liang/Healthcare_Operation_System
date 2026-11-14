# HealthOps — Healthcare Operations Dashboard

A Java / Spring Boot web application for running COVID-19 vaccination operations: patient registry,
vaccination records, vaccine lot inventory and shipments, clinic sites, and clinical staff assignments.
It ships with a server-rendered dashboard UI and a JSON REST API over the same MySQL database.

![Dashboard](docs/screenshots/dashboard.png)

| Add patient dialog | Clinics (dark mode) |
| --- | --- |
| ![Add patient](docs/screenshots/add-patient.png) | ![Clinics in dark mode](docs/screenshots/clinics-dark.png) |

## Features

- **Dashboard**: patient count, vaccination coverage, clinic and staff totals, recent vaccinations, doses by manufacturer, and a per-clinic supply/staffing status table
- **Patients**: searchable directory showing each patient's vaccination status, vaccine, clinic and appointment, plus forms to register or remove patients
- **Vaccinations**: record a dose for a patient who doesn't have one yet (patient, clinic, lot, date and time), and delete records
- **Vaccine inventory**: lots with production and expiry dates, an automatic *Expired* badge, dose totals, and shipment assignment from lot to clinic
- **Clinics**: a card for each site with its address, operating date, shipped lots, staff count and doses given
- **Clinical staff**: nurses and doctors with credentials and clinic assignments, filterable by role
- **REST API**: CRUD for every resource, with field-level validation errors and clear 404 / 409 responses
- Responsive layout that becomes a top navigation bar on phones, automatic dark mode, and accessible dialogs, labels and focus states
- When MySQL is down, pages show a friendly message and the API returns 503, instead of crashing

## Tech stack

| Layer | Technology |
| --- | --- |
| Language / runtime | Java 21 |
| Framework | Spring Boot 3.3 (Spring MVC, Bean Validation) |
| Views | Thymeleaf templates, hand-written CSS design system, vanilla JS |
| Data access | Spring `JdbcClient` with plain SQL over the existing schema |
| Database | MySQL 8 (production), H2 in MySQL mode (`demo` profile and tests) |
| Tests | JUnit 5 + MockMvc, Playwright (UI + API), Jest |
| CI | GitHub Actions with a MySQL service container |

## Project structure

```text
.
|-- database/covidDB.sql              MySQL schema + sample data
|-- pom.xml
|-- src/main/java/com/healthops/
|   |-- config/                      input trimming for forms and JSON
|   |-- domain/                      records: Patient, Clinic, Vaccine, Worker, ...
|   |-- repository/                  SQL via JdbcClient, one class per aggregate
|   `-- web/
|       |-- api/                     REST controllers + JSON error handling
|       |-- form/                    validated request/form objects
|       `-- page/                    Thymeleaf page controllers
|-- src/main/resources/
|   |-- application.yml              MySQL settings (env-var driven)
|   |-- application-demo.yml         in-memory H2 profile
|   |-- db/                          H2 schema + seed data for the demo profile
|   |-- static/                      css/app.css, js/app.js, favicon
|   `-- templates/                   Thymeleaf pages + shared fragments
|-- src/test/java/                   JUnit / MockMvc integration tests
|-- tests/e2e/                       Playwright UI + API tests
|-- tests/unit/                      Jest tests for app.js
`-- qa/                              test plan, cases, traceability, bug reports
```

## Running locally

Requirements: **JDK 21** and **Maven 3.9+**. Node.js 20 is only needed for the JavaScript tests.

### Option 1: demo mode (no database to install)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

Open http://localhost:8080. Demo mode uses an in-memory H2 database loaded with the same sample data as
`database/covidDB.sql`. Changes are lost when the app stops.

### Option 2: MySQL

Import the database:

```bash
mysql -u root < database/covidDB.sql
```

Start the app:

```bash
mvn spring-boot:run
```

Connection settings come from environment variables:

| Variable | Default |
| --- | --- |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `covidDB` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | *(empty)* |
| `PORT` | `8080` |

To build a runnable jar:

```bash
mvn package
```

```bash
java -jar target/healthops.jar
```

## REST API

All endpoints accept and return JSON. Dates use `YYYY-MM-DD` and times use `HH:MM`.

| Resource | Endpoints |
| --- | --- |
| Health | `GET /api/health` |
| Patients | `GET/POST /api/patients`, `GET/PUT/DELETE /api/patients/{ohip}` |
| Clinics | `GET/POST /api/clinics`, `GET/PUT/DELETE /api/clinics/{name}` |
| Vaccine lots | `GET/POST /api/vaccines`, `GET/PUT/DELETE /api/vaccines/{lot}` |
| Shipments | `GET/POST /api/shipments`, `GET/DELETE /api/shipments/{lot}/{clinic}` |
| Vaccinations | `GET/POST /api/vaccinations`, `GET/PUT/DELETE /api/vaccinations/{ohip}` |
| Staff | `GET /api/workers[?role=nurse\|doctor]`, `POST /api/workers`, `GET/PUT/DELETE /api/workers/{role}/{id}` |

Example:

```bash
curl -X POST localhost:8080/api/patients -H 'Content-Type: application/json' \
  -d '{"ohip":"1234-567-890","firstName":"Grace","lastName":"Hopper","dob":"1985-12-09"}'
```

Status codes: `201` created (with a `Location` header), `204` deleted, `400` malformed JSON,
`404` not found, `409` duplicate or still-referenced record, `422` validation failed, `503` database unavailable.
A validation error looks like this:

```json
{
  "status": 422,
  "error": "Please correct the invalid fields.",
  "fieldErrors": { "ohip": "OHIP must use the format 0000-000-000." }
}
```

## Testing

```bash
mvn verify
```

This runs the JUnit + MockMvc tests: REST CRUD, validation, conflicts, page rendering, the post/redirect/get form flow,
and behavior when MySQL is down.

```bash
npm install
```

```bash
npm run build
```

```bash
npm test
```

`npm test` runs the Jest unit tests and the Playwright UI and API tests. Playwright starts the packaged jar in
`demo` mode automatically. To run the end-to-end tests against MySQL instead, set `SPRING_PROFILES_ACTIVE=default`
(plus the `DB_*` variables if needed). To test an app that is already running, set `BASE_URL`.

```bash
npm run test:report
```

This opens the latest Playwright HTML report.

QA references: `qa/test-plan.md`, `qa/test-cases.md`, `qa/traceability-matrix.md`, `qa/bug-report-template.md`,
`qa/sample-bug-reports.md`, `qa/ai-test-review-checklist.md`, `qa/platform-scope.md`.

## Notes on the data model

The schema in `database/covidDB.sql` is used as-is, so the app adapts to a few of its quirks:

- Dates are stored as text in mixed formats (`2021-12-21`, `01 AUG 2022`). The UI parses all of them and shows one readable format.
- `ShipTo.Lots` and `Vaccination.Lots` are `CHAR(6)`, so lot codes are limited to 6 letters or digits.
- A doctor can be assigned to more than one clinic. The staff page lists each person once, with all of their clinics.
- Each patient can have only one vaccination record (`Vaccination.OHIP` is the primary key), so the vaccination form only offers patients who don't have one yet.
