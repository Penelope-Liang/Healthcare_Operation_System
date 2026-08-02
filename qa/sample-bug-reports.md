# Sample Bug Reports

These examples use a Jira-style defect format to show how issues would be documented in a bug-tracking tool.

## BUG-001: Patient Form Accepts Invalid OHIP Format

## Environment

- Browser: Chromium
- OS: macOS
- App URL: `/patients.php`
- Database state: `covidDB` imported
- Commit: local test build

## Steps to Reproduce

1. Open `/patients.php`.
2. Enter `123` in the OHIP field.
3. Fill first name, last name, and date of birth with valid values.
4. Submit the form.

## Expected Result

The form blocks submission and asks for OHIP format `0000-000-000`.

## Actual Result

The invalid value is accepted.

## Evidence

- Related automated check: `tests/e2e/patients.spec.js`
- Requirement: REQ-003

## Severity

High

## BUG-002: Vaccine Dose Allows Negative Inventory

## Environment

- Browser: Chromium
- OS: macOS
- App URL: `/vaccines.php`
- Database state: `covidDB` imported
- Commit: local test build

## Steps to Reproduce

1. Open `/vaccines.php`.
2. Enter a valid lot, manufacturer, production date, and expiry date.
3. Enter `-1` in Doses.
4. Submit the form.

## Expected Result

The UI or server rejects negative dose counts.

## Actual Result

The negative dose count is saved.

## Evidence

- Related automated checks: `tests/e2e/form-boundaries.spec.js`, `tests/e2e/database-write.spec.js`
- Requirement: REQ-009

## Severity

High

## BUG-003: Patient API Allows Update Without Required Name

## Environment

- Client: Playwright request API
- OS: macOS
- App URL: `/api/patients.php`
- Database state: `covidDB` imported
- Commit: local test build

## Steps to Reproduce

1. Create a patient through `POST /api/patients.php`.
2. Send `PUT /api/patients.php?OHIP={created-ohip}` with an empty `FirstName`.
3. Check the API response.

## Expected Result

The API returns `422` with a required field validation error.

## Actual Result

The API updates the patient with incomplete data.

## Evidence

- Related automated check: `tests/e2e/database-write.spec.js`
- Requirement: REQ-012

## Severity

Medium
