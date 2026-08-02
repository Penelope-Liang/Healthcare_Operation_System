# Test Cases

| ID | Area | Scenario | Steps | Expected Result | Automation |
| --- | --- | --- | --- | --- | --- |
| TC-001 | Dashboard | Dashboard loads | Open `/covid.php` | Dashboard heading and module cards are visible | Playwright |
| TC-002 | Navigation | Core navigation links work | Open dashboard, click Patients, then Reports | URL changes to the selected module | Playwright |
| TC-003 | Patients | OHIP format validation | Enter `123` in OHIP and submit | Browser blocks submission for pattern mismatch | Playwright |
| TC-004 | Patients | Patient table filter binding | Open Patients page | Filter input targets `#patients-table` | Playwright |
| TC-005 | JavaScript | Table filter hides non-matching rows | Type a matching query into a filter input | Matching row stays visible; non-matching row is hidden | Jest |
| TC-006 | Vaccines | Vaccine lot create form exists | Open Vaccines page | Add Vaccine Lot form and submit button are visible | Playwright |
| TC-007 | Vaccines | Shipment assignment form exists | Open Vaccines page | Assign Shipment form and submit button are visible | Playwright |
| TC-008 | Clinics | Clinic create form exists | Open Clinics page | Add Clinic form and submit button are visible | Playwright |
| TC-009 | Workers | Worker assignment create form exists | Open Workers page | Role, credential, clinic fields, and submit button are visible | Playwright |
| TC-010 | Reports | Vaccination record create form exists | Open Reports page | Patient, clinic, lot, date, time fields, and submit button are visible | Playwright |
| TC-011 | Error Handling | Database unavailable state | Stop MySQL and open dashboard | Page renders with a database error notice instead of a blank screen | Playwright |
| TC-012 | Patients | Required patient fields | Submit patient form with missing OHIP, first name, last name, or DOB | Browser validation blocks submission | Playwright |
| TC-013 | Patients | Patient name boundary | Enter numbers in first or last name | Browser validation reports pattern mismatch | Playwright |
| TC-014 | Vaccines | Vaccine dose boundary | Enter a negative dose count | Browser validation reports range underflow | Playwright |
| TC-015 | Clinics | Required clinic fields | Submit clinic form with missing name or operating date | Browser validation blocks submission | Playwright |
| TC-016 | Workers | Required worker assignment fields | Submit worker form with missing role, ID, credential, or clinic | Browser validation blocks submission | Playwright |
| TC-017 | Reports | Required vaccination fields | Submit vaccination form with missing patient, clinic, lot, date, or time | Browser validation blocks submission | Playwright |
| TC-018 | Patients | Patient create/read persistence | Create a unique patient, filter patient table by OHIP | New patient appears in the table | Playwright DB |
| TC-019 | Clinics | Clinic create/read persistence | Create a unique clinic, filter clinic table by name | New clinic appears in the table | Playwright DB |
| TC-020 | Vaccines | Vaccine create/read persistence | Create a unique vaccine lot, filter inventory by lot | New lot appears in the table | Playwright DB |
| TC-021 | Server Validation | Invalid direct POST is rejected | POST invalid vaccine date and negative dose directly to `vaccines.php` | Server returns validation notice before insert | Playwright DB |
| TC-022 | API | API health check | GET `/api/health.php` | JSON response returns status and database state | Playwright API |
| TC-023 | API | Patient API create/read | POST a unique patient to `/api/patients.php`, then GET patients | API returns 201 and patient appears in list | Playwright DB API |
| TC-024 | API | Patient API validation | POST invalid patient JSON | API returns validation error | Playwright DB API |
