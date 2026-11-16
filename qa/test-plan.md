# Test Plan

## Scope

This project is a Java 21 / Spring Boot REST API (JDBC, MySQL) with a Vue 3 single-page frontend. Current automation scope is backend integration testing with JUnit and MockMvc, frontend unit testing with Vitest, and browser UI plus REST API validation with Playwright.

Mobile iOS and Android automation are outside the current application scope because this repository does not contain mobile apps.

## Test Objectives

- Verify core pages load and navigation works.
- Verify patient form validation blocks invalid OHIP formats.
- Verify operational create forms are present for patients, vaccines, clinics, staff, shipments, and vaccination records.
- Verify search boxes filter their tables and card grids.
- Verify pages render a usable error state when MySQL is unavailable.
- Verify selected create flows persist records to MySQL and render them back in the UI.
- Verify REST API health, resource CRUD, and API validation behavior.
- Verify GitHub Actions can run the QA checks and publish Playwright HTML report artifacts.

## Tools

- Playwright for browser end-to-end tests.
- Playwright request testing for REST API checks.
- Vitest with jsdom and Vue Test Utils for frontend logic and component tests.
- JUnit 5 + MockMvc for API and backend integration tests.
- JDK 21 + Maven, with MySQL or the in-memory H2 `demo` profile for local execution.
- GitHub Actions for CI execution on push and pull request.

## Entry Criteria

- `mvn verify` passes (compilation plus JUnit/MockMvc integration tests).
- The app can start from the `public/` web root.
- MySQL is running when database-backed create scenarios are tested.
- The `healthops` database has been created from `database/schema.sql` and `database/seed.sql` before running the end-to-end tests with `SPRING_PROFILES_ACTIVE=default` (not needed for the H2 `demo` profile).

## Exit Criteria

- Navigation and smoke tests pass.
- Form validation tests pass.
- Table filtering tests pass.
- Any failing test has a defect report or a documented product decision.
- Database-backed create/read and REST API CRUD tests pass when MySQL is included in the test run.
- CI uploads a Playwright HTML report artifact for review.
