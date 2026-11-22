# Platform Scope

## Current Repository

This repository contains a web application only:

- Vue 3 single-page frontend served by Spring Boot
- MySQL schema
- Browser JavaScript
- CSS
- REST-style JSON endpoints under `public/api/`

## Automated Coverage Added

- Web UI automation with Playwright
- REST API automation with Playwright request tests
- Frontend unit tests with Vitest, jsdom and Vue Test Utils
- QA artifacts for test cases, defect reporting, traceability, and AI-assisted test review
- Database-backed create/read tests for selected web workflows
- Database-backed REST API CRUD tests for patients, clinics, vaccine lots, staff, shipments, and vaccination records
- GitHub Actions pipelines for JUnit (with a JaCoCo coverage gate), Vitest, Playwright UI/API tests against MySQL, and Docker image build, smoke test and publish; Dependabot dependency updates

## Not In Scope

- iOS automation
- Android automation

iOS and Android are not represented in this codebase. They should not be claimed as completed work for this project unless mobile apps are added later.
