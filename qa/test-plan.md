# Test Plan

## Scope

This project is a PHP/MySQL web application with small REST-style JSON endpoints. Current automation scope is web UI validation with Playwright, REST API validation with Playwright request tests, and JavaScript DOM behavior validation with Jest.

Mobile iOS and Android automation are outside the current application scope because this repository does not contain mobile apps.

## Test Objectives

- Verify core pages load and navigation works.
- Verify patient form validation blocks invalid OHIP formats.
- Verify operational create forms are present for patients, vaccines, clinics, workers, shipments, and vaccination records.
- Verify searchable tables are connected to the table-filter behavior.
- Verify pages render a usable error state when MySQL is unavailable.
- Verify selected create flows persist records to MySQL and render them back in the UI.
- Verify REST API health, patient CRUD, and API validation behavior.
- Verify GitHub Actions can run the QA checks and publish Playwright HTML report artifacts.

## Tools

- Playwright for browser end-to-end tests.
- Playwright request testing for REST API checks.
- Jest with jsdom for JavaScript behavior tests.
- XAMPP PHP server and MySQL for local execution.
- GitHub Actions for CI execution on push and pull request.

## Entry Criteria

- PHP files pass syntax checks.
- The app can start from the `public/` web root.
- MySQL is running when database-backed create scenarios are tested.
- `covidDB` has been imported before running `npm run test:db`.

## Exit Criteria

- Navigation and smoke tests pass.
- Form validation tests pass.
- Table filtering tests pass.
- Any failing test has a defect report or a documented product decision.
- Database-backed create/read tests pass when MySQL is included in the test run.
- CI uploads a Playwright HTML report artifact for review.
