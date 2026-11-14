# Test Plan

## Scope

This project is a Java 21 / Spring Boot web application (Thymeleaf pages, JDBC, MySQL) with a JSON REST API. Current automation scope is web UI validation with Playwright, REST API validation with Playwright request tests, and JavaScript DOM behavior validation with Jest.

Mobile iOS and Android automation are outside the current application scope because this repository does not contain mobile apps.

## Test Objectives

- Verify core pages load and navigation works.
- Verify patient form validation blocks invalid OHIP formats.
- Verify operational create forms are present for patients, vaccines, clinics, workers, shipments, and vaccination records.
- Verify searchable tables are connected to the table-filter behavior.
- Verify pages render a usable error state when MySQL is unavailable.
- Verify selected create flows persist records to MySQL and render them back in the UI.
- Verify REST API health, resource CRUD, and API validation behavior.
- Verify GitHub Actions can run the QA checks and publish Playwright HTML report artifacts.

## Tools

- Playwright for browser end-to-end tests.
- Playwright request testing for REST API checks.
- Jest with jsdom for JavaScript behavior tests.
- JDK 21 + Maven, with MySQL or the in-memory H2 `demo` profile for local execution.
- GitHub Actions for CI execution on push and pull request.

## Entry Criteria

- `mvn verify` passes (compilation plus JUnit/MockMvc integration tests).
- The app can start from the `public/` web root.
- MySQL is running when database-backed create scenarios are tested.
- `covidDB` has been imported before running the end-to-end tests with `SPRING_PROFILES_ACTIVE=default` (not needed for the H2 `demo` profile).

## Exit Criteria

- Navigation and smoke tests pass.
- Form validation tests pass.
- Table filtering tests pass.
- Any failing test has a defect report or a documented product decision.
- Database-backed create/read and REST API CRUD tests pass when MySQL is included in the test run.
- CI uploads a Playwright HTML report artifact for review.
