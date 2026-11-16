import { describe, expect, test } from "vitest";
import { ageInYears, isPast, parseDate, prettyDate } from "@/lib/dates";

describe("parseDate", () => {
  test("understands every format stored in the legacy schema", () => {
    expect(parseDate("2021-12-21")?.toISOString()).toBe("2021-12-21T00:00:00.000Z");
    expect(parseDate("01 AUG 2022")?.toISOString()).toBe("2022-08-01T00:00:00.000Z");
    expect(parseDate("01 JULY 2021")?.toISOString()).toBe("2021-07-01T00:00:00.000Z");
  });

  test("rejects garbage and impossible dates", () => {
    expect(parseDate("someday")).toBeNull();
    expect(parseDate("2021-02-31")).toBeNull();
    expect(parseDate("01 AUGUSTUS 2022")).toBeNull();
    expect(parseDate(null)).toBeNull();
  });
});

describe("display helpers", () => {
  const now = new Date(2026, 10, 16);

  test("prettyDate formats parsed dates and falls back to the raw text", () => {
    expect(prettyDate("01 AUG 2022")).toBe("Aug 1, 2022");
    expect(prettyDate("someday")).toBe("someday");
  });

  test("isPast compares against today", () => {
    expect(isPast("01 AUG 2022", now)).toBe(true);
    expect(isPast("2027-01-01", now)).toBe(false);
  });

  test("ageInYears accounts for birthdays later in the year", () => {
    expect(ageInYears("1990-01-23", now)).toBe(36);
    expect(ageInYears("1990-12-01", now)).toBe(35);
    expect(ageInYears("unknown", now)).toBeNull();
  });
});
