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
