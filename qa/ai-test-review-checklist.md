# AI-Assisted Test Review Checklist

Use this checklist before committing AI-generated or AI-assisted tests.

## Coverage

- Does the test map to a real requirement or defect?
- Does the test check business behavior, not only that elements exist?
- Are edge cases covered for required fields, invalid formats, and missing database state?

## Reliability

- Does the test use stable locators such as roles, labels, or IDs?
- Does the test avoid fixed sleeps?
- Does the test avoid relying on row order unless order is part of the requirement?

## Data Integrity

- Does the test avoid polluting shared data unless cleanup exists?
- Does the test use unique data when creating records?
- Does the test verify the saved result after submit when database-backed validation is in scope?

## Review Outcome

- Approved:
- Changes required:
- Reviewer:
- Date:
