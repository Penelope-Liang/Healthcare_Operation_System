# Test Plan

## Scope

This project is a PHP/MySQL web application. Current automation scope is web UI validation with Playwright and JavaScript DOM behavior validation with Jest.

Mobile iOS, Android, and API automation are outside the current application scope because this repository does not contain mobile apps or API endpoints.

## Test Objectives

- Verify core pages load and navigation works.
- Verify patient form validation blocks invalid OHIP formats.
- Verify operational create forms are present for patients, vaccines, clinics, workers, shipments, and vaccination records.
- Verify searchable tables are connected to the table-filter behavior.
- Verify pages render a usable error state when MySQL is unavailable.

## Tools

- Playwright for browser end-to-end tests.
- Jest with jsdom for JavaScript behavior tests.
- XAMPP PHP server and MySQL for local execution.

## Entry Criteria

- PHP files pass syntax checks.
- The app can start from the `public/` web root.
- MySQL is running when database-backed create scenarios are tested.

## Exit Criteria

- Navigation and smoke tests pass.
- Form validation tests pass.
- Table filtering tests pass.
- Any failing test has a defect report or a documented product decision.
