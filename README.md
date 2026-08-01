# Healthcare Operations Dashboard

A PHP and MySQL web application for managing COVID-19 healthcare operations, including patient records, vaccine logistics, clinic sites, clinical workers, and operational reports.

The project was redesigned from a basic COVID-19 database interface into a cleaner healthcare operations dashboard with consistent navigation, responsive UI, searchable tables, and form validation.

## Features

- Dashboard summary for patients, vaccination records, clinics, and clinical workers
- Patient management page with searchable vaccination history
- Add patient form with required field and format validation
- Vaccine inventory page with lot, manufacturer, production, expiry, dose, and shipment data
- Worker assignment page for nurses and doctors across vaccination clinics
- Clinic directory with addresses, operating dates, and linked vaccine shipments
- Reports page with vaccination activity and staffing summaries
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
|-- covidDB.sql
|-- php/
|   |-- app.js
|   |-- clinics.php
|   |-- connectdb.php
|   |-- covid.php
|   |-- index.php
|   |-- layout.php
|   |-- patients.php
|   |-- reports.php
|   |-- styles.css
|   |-- vaccines.php
|   `-- workers.php
`-- pic/
```

Legacy PHP pages are still included for compatibility with the original project flow.

## Database Setup

The application expects a MySQL database named `covidDB`.

Default connection settings are defined in `php/connectdb.php`:

```text
host: localhost
database: covidDB
user: root
password: empty
```

To import the database with XAMPP:

```bash
/Applications/XAMPP/xamppfiles/bin/mysql -u root < covidDB.sql
```

If `covidDB` already exists and needs to be recreated:

```bash
/Applications/XAMPP/xamppfiles/bin/mysql -u root -e "DROP DATABASE IF EXISTS covidDB;"
/Applications/XAMPP/xamppfiles/bin/mysql -u root < covidDB.sql
```

## Run Locally

Option 1: Use XAMPP Apache.

Place the project inside:

```text
/Applications/XAMPP/xamppfiles/htdocs/
```

Then start Apache and MySQL in XAMPP and open:

```text
http://localhost/Healthcare_Operation_System/php/
```

Option 2: Use the XAMPP PHP development server.

From the project root:

```bash
/Applications/XAMPP/xamppfiles/bin/php -S localhost:8000 -t php
```

Then open:

```text
http://localhost:8000/covid.php
```

## Core Workflows

### Patient Management

Users can review patient records, search across patient and vaccination data, and create new patient records with basic server-side validation.

### Vaccine Logistics

Users can review vaccine lots, manufacturers, dose counts, production dates, expiry dates, and clinic shipment relationships.

### Clinic Network

Users can view clinic locations, addresses, operating dates, and assigned vaccine lots.

### Worker Assignments

Users can review nurse and doctor assignments by vaccination clinic.

### Operational Reports

Users can review overall system counts, vaccination schedule data, and clinical staffing summaries.

## Automation Testing Roadmap

Automated testing is planned as a backend project quality layer, not as a user-facing website page.

Planned Playwright coverage:

- Dashboard smoke checks
- Navigation checks across core pages
- Patient form validation
- Patient table filtering
- Vaccine inventory rendering
- Clinic shipment rendering
- Worker assignment rendering
- Unsafe input regression checks

Planned structure:

```text
tests/
|-- dashboard.spec.js
|-- patients.spec.js
|-- vaccines.spec.js
|-- workers.spec.js
`-- security.spec.js
```

## Current Status

The main website UI has been redesigned and the database-backed pages are ready to run with XAMPP MySQL. Automated Playwright tests are the next implementation step.
