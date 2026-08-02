# Requirement Traceability Matrix

| Requirement ID | Requirement | Test Case IDs | Automated Coverage |
| --- | --- | --- | --- |
| REQ-001 | Users can access dashboard metrics and module navigation. | TC-001, TC-002 | `tests/e2e/navigation.spec.js` |
| REQ-002 | Users can view and search patient records. | TC-004, TC-005 | `tests/e2e/patients.spec.js`, `tests/unit/table-filter.test.js` |
| REQ-003 | Patient creation blocks invalid OHIP values. | TC-003 | `tests/e2e/patients.spec.js` |
| REQ-004 | Users can manage vaccine lots and shipments. | TC-006, TC-007 | `tests/e2e/operations-create-forms.spec.js` |
| REQ-005 | Users can manage clinic sites. | TC-008 | `tests/e2e/operations-create-forms.spec.js` |
| REQ-006 | Users can manage nurse and doctor assignments. | TC-009 | `tests/e2e/operations-create-forms.spec.js` |
| REQ-007 | Users can manage vaccination records. | TC-010 | `tests/e2e/operations-create-forms.spec.js` |
| REQ-008 | Pages show a usable error state when MySQL is unavailable. | TC-011 | `tests/e2e/error-handling.spec.js` |
| REQ-009 | Required fields and boundary constraints block invalid form submissions. | TC-012, TC-013, TC-014, TC-015, TC-016, TC-017 | `tests/e2e/form-boundaries.spec.js` |
| REQ-010 | Create actions persist valid records and render them back in the UI. | TC-018, TC-019, TC-020 | `tests/e2e/database-write.spec.js` |
| REQ-011 | Server-side validation rejects invalid direct POST payloads. | TC-021 | `tests/e2e/database-write.spec.js` |
| REQ-012 | REST API endpoints return JSON and support patient create/read validation. | TC-022, TC-023, TC-024 | `tests/e2e/api.spec.js`, `tests/e2e/database-write.spec.js` |
