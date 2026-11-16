import { describe, expect, test } from "vitest";
import { ageInYears, isPast, parseDate, prettyDate } from "@/lib/dates";

describe("parseDate", () => {
  test("parses ISO dates from the API", () => {
    expect(parseDate("2021-12-21")?.toISOString()).toBe("2021-12-21T00:00:00.000Z");
  });

  test("rejects other formats and impossible dates", () => {
    expect(parseDate("01 AUG 2022")).toBeNull();
    expect(parseDate("2021-02-31")).toBeNull();
    expect(parseDate(null)).toBeNull();
  });
});

describe("display helpers", () => {
  const now = new Date(2026, 10, 16);

  test("prettyDate formats ISO dates and falls back to the raw text", () => {
    expect(prettyDate("2022-08-01")).toBe("Aug 1, 2022");
    expect(prettyDate("someday")).toBe("someday");
    expect(prettyDate(null)).toBe("");
  });

  test("isPast compares against today", () => {
    expect(isPast("2022-08-01", now)).toBe(true);
    expect(isPast("2026-11-16", now)).toBe(false);
    expect(isPast("2027-01-01", now)).toBe(false);
  });

  test("ageInYears accounts for birthdays later in the year", () => {
    expect(ageInYears("1990-01-23", now)).toBe(36);
    expect(ageInYears("1990-12-01", now)).toBe(35);
    expect(ageInYears("unknown", now)).toBeNull();
  });
});
