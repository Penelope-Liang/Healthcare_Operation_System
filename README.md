# Healthcare Operations Dashboard

A PHP and MySQL web application for managing COVID-19 healthcare operations, including patient records, vaccine logistics, clinic sites, clinical workers, and operational reports.

The project was redesigned from a basic COVID-19 database interface into a cleaner healthcare operations dashboard with consistent navigation, responsive UI, searchable tables, and form validation.

## Features

- Dashboard summary for patients, vaccination records, clinics, and clinical workers
- Patient management page with searchable vaccination history
- Add patient form with required field and format validation
- Vaccine inventory page with lot, manufacturer, production, expiry, dose, shipment data, and forms for creating lots and assigning shipments
- Worker assignment page for nurses and doctors across vaccination clinics with a form for creating worker assignments
- Clinic directory with addresses, operating dates, linked vaccine shipments, and a form for creating clinic sites
- Reports page with vaccination activity, staffing summaries, and a form for creating vaccination records
- REST API endpoints for health checks and patient CRUD workflows
- Graceful database connection messaging when MySQL is not running

## Tech Stack

- PHP
- MySQL
- PDO
- HTML
- CSS
- JavaScript

## Project Structure

```text
.
|-- database/
|   `-- covidDB.sql
|-- includes/
|   |-- connectdb.php
|   `-- layout.php
|-- qa/
|   |-- ai-test-review-checklist.md
|   |-- bug-report-template.md
|   |-- platform-scope.md
|   |-- test-cases.md
|   |-- test-plan.md
|   `-- traceability-matrix.md
|-- public/
|   |-- api/
|   |-- assets/
|   |   |-- app.js
|   |   `-- styles.css
|   |-- clinics.php
|   |-- covid.php
|   |-- index.php
|   |-- legacy/
|   |-- patients.php
|   |-- reports.php
|   |-- vaccines.php
|   `-- workers.php
|-- tests/
|   |-- e2e/
|   `-- unit/
|-- jest.config.js
|-- package.json
|-- playwright.config.js

```

The `public/` directory is the web root. Shared PHP includes live in `includes/`, static browser assets live in `public/assets/`, database setup lives in `database/`, and legacy PHP pages live in `public/legacy/` for reference only. Legacy page access is blocked by Apache `.htaccess`.

## Database Setup

The application expects a MySQL database named `covidDB`.

Default connection settings are defined in `includes/connectdb.php`:

```text
host: localhost
database: covidDB
user: root
password: empty
```

To import the database with XAMPP:

```bash
/Applications/XAMPP/xamppfiles/bin/mysql -u root < database/covidDB.sql
```

If `covidDB` already exists and needs to be recreated:

```bash
/Applications/XAMPP/xamppfiles/bin/mysql -u root -e "DROP DATABASE IF EXISTS covidDB;"
/Applications/XAMPP/xamppfiles/bin/mysql -u root < database/covidDB.sql
```

## Run Locally

Option 1: Use XAMPP Apache.

Place the project inside:

```text
/Applications/XAMPP/xamppfiles/htdocs/
```

Then start Apache and MySQL in XAMPP and open:

```text
http://localhost/Healthcare_Operation_System/public/
```

Option 2: Use the XAMPP PHP development server.

From the project root:

```bash
/Applications/XAMPP/xamppfiles/bin/php -S localhost:8000 -t public
```

Then open:

```text
http://localhost:8000/covid.php
```

## Core Workflows

### Patient Management

Users can review patient records, search across patient and vaccination data, and create new patient records with basic server-side validation.

### Vaccine Logistics

Users can review vaccine lots, manufacturers, dose counts, production dates, expiry dates, and clinic shipment relationships. Users can also create vaccine lots and assign existing lots to clinic sites.

### Clinic Network

Users can view clinic locations, addresses, operating dates, and assigned vaccine lots. Users can also create new clinic site records.

### Worker Assignments

Users can review nurse and doctor assignments by vaccination clinic. Users can also create nurse or doctor records with credentials and clinic assignments.

### Operational Reports

Users can review overall system counts, vaccination schedule data, and clinical staffing summaries. Users can also create vaccination records by selecting an existing patient, clinic, and vaccine lot.

## Automation and QA

The repository includes JavaScript automation and QA documentation for the web application.

Automated coverage:

- Playwright end-to-end tests for dashboard loading, navigation, patient validation, operations create forms, and database error handling
- Playwright API tests for REST API health checks and database-backed patient CRUD validation
- Jest unit tests for browser table filtering behavior
- QA documentation for test cases, defect reporting, traceability, platform scope, and AI-assisted test review
- GitHub Actions workflow for PHP lint, Jest, Playwright, API, and database-backed test execution

Install test dependencies:

```bash
npm install
```

Run unit tests:

```bash
npm run test:unit
```

Run end-to-end tests:

```bash
npm run test:e2e
```

Run all tests:

```bash
npm test
```

Run database-backed create/read tests:

```bash
npm run test:db
```

Open the latest Playwright HTML report:

```bash
npm run test:report
```

`npm run test:db` requires XAMPP MySQL to be running with the `covidDB` database imported. These tests create unique patient, clinic, and vaccine records, verify records render back in the UI, and validate REST API patient CRUD behavior.

QA references:

- `qa/test-plan.md`
- `qa/test-cases.md`
- `qa/traceability-matrix.md`
- `qa/bug-report-template.md`
- `qa/sample-bug-reports.md`
- `qa/ai-test-review-checklist.md`
- `qa/platform-scope.md`

## Current Status

The main website UI has database-backed create forms for the core web modules, and the repository now includes Playwright, Jest, and QA documentation for web automation practice.
