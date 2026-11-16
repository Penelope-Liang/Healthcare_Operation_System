import { describe, expect, test } from "vitest";
import { matchesQuery, plural, splitList } from "@/lib/text";

describe("matchesQuery", () => {
  test("is case-insensitive and matches any value", () => {
    expect(matchesQuery("amy", "Amy White", "1029-293-345")).toBe(true);
    expect(matchesQuery("293", "Amy White", "1029-293-345")).toBe(true);
    expect(matchesQuery("john", "Amy White", null)).toBe(false);
  });

  test("an empty query matches everything", () => {
    expect(matchesQuery("   ", "anything")).toBe(true);
  });
});

describe("splitList", () => {
  test("splits comma-separated input, trimming blanks and duplicates", () => {
    expect(splitList(" RN, BSN ,, rn ,CCRN")).toEqual(["RN", "BSN", "CCRN"]);
    expect(splitList("")).toEqual([]);
    expect(splitList(undefined)).toEqual([]);
  });
});

describe("plural", () => {
  test("adds an s except for one", () => {
    expect(plural(1, "dose")).toBe("1 dose");
    expect(plural(0, "dose")).toBe("0 doses");
    expect(plural(3, "patient")).toBe("3 patients");
  });
});
