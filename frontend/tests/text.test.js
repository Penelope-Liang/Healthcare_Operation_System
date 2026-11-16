import { describe, expect, test } from "vitest";
import { groupStaff, matchesQuery } from "@/lib/text";

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

describe("groupStaff", () => {
  test("merges one-row-per-clinic results into one entry per person", () => {
    const staff = groupStaff([
      { role: "doctor", id: "XC546", firstName: "Cindy", lastName: "Young", credential: "CNNP", clinicName: "Frontenac" },
      { role: "doctor", id: "XC546", firstName: "Cindy", lastName: "Young", credential: "CNNP", clinicName: "Quarry" },
      { role: "nurse", id: "XC546", firstName: "Same", lastName: "Id", credential: "RN", clinicName: "Rexall" },
      { role: "doctor", id: "VB980", firstName: "Sherry", lastName: "Fox", credential: "MBBS", clinicName: null }
    ]);

    expect(staff).toHaveLength(3);
    expect(staff[0].clinics).toEqual(["Frontenac", "Quarry"]);
    expect(staff[1].role).toBe("nurse");
    expect(staff[2].clinics).toEqual([]);
  });
});
