# Platform Scope

## Current Repository

This repository contains a web application only:

- PHP pages
- MySQL schema
- Browser JavaScript
- CSS
- REST-style JSON endpoints under `public/api/`

## Automated Coverage Added

- Web UI automation with Playwright
- REST API automation with Playwright request tests
- JavaScript unit tests with Jest and jsdom
- QA artifacts for test cases, defect reporting, traceability, and AI-assisted test review
- Database-backed create/read tests for selected web workflows
- Database-backed REST API CRUD tests for patients, clinics, vaccine lots, workers, shipments, and vaccination records
- GitHub Actions pipeline for Jest, Playwright, API, and database-backed tests

## Not In Scope

- iOS automation
- Android automation

iOS and Android are not represented in this codebase. They should not be claimed as completed work for this project unless mobile apps are added later.
