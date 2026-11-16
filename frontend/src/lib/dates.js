/**
 * Dates in the legacy schema are text in mixed formats ("2021-12-21", "01 AUG 2022",
 * "01 JULY 2021"). These helpers parse any of them and render one readable format.
 */
const MONTHS = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];
const PRETTY = new Intl.DateTimeFormat("en-US", { month: "short", day: "numeric", year: "numeric", timeZone: "UTC" });

export function parseDate(text) {
  if (!text) {
    return null;
  }
  const value = String(text).trim();

  let m = value.match(/^(\d{4})-(\d{2})-(\d{2})$/);
  if (m) {
    return valid(+m[1], +m[2], +m[3]);
  }
  m = value.match(/^(\d{1,2})\s+([A-Za-z]+)\s+(\d{4})$/);
  if (m) {
    const name = m[2].toLowerCase();
    const month = MONTHS.indexOf(name.slice(0, 3)) + 1;
    const fullName = new Intl.DateTimeFormat("en-US", { month: "long", timeZone: "UTC" })
      .format(Date.UTC(2000, month - 1, 1)).toLowerCase();
    if (month === 0 || (name.length > 3 && name !== fullName)) {
      return null;
    }
    return valid(+m[3], month, +m[1]);
  }
  return null;
}

function valid(year, month, day) {
  const date = new Date(Date.UTC(year, month - 1, day));
  return date.getUTCFullYear() === year && date.getUTCMonth() === month - 1 && date.getUTCDate() === day ? date : null;
}

/** A readable date, or the original text if it can't be parsed. */
export function prettyDate(text) {
  const date = parseDate(text);
  return date ? PRETTY.format(date) : (text ?? "");
}

function todayUtc(now = new Date()) {
  return Date.UTC(now.getFullYear(), now.getMonth(), now.getDate());
}

export function isPast(text, now = new Date()) {
  const date = parseDate(text);
  return date !== null && date.getTime() < todayUtc(now);
}

/** Whole years between the date and today. */
export function ageInYears(text, now = new Date()) {
  const date = parseDate(text);
  if (!date) {
    return null;
  }
  let age = now.getFullYear() - date.getUTCFullYear();
  const beforeBirthday = now.getMonth() < date.getUTCMonth()
    || (now.getMonth() === date.getUTCMonth() && now.getDate() < date.getUTCDate());
  return beforeBirthday ? age - 1 : age;
}
