/** Date helpers. The API returns dates as ISO strings ("2021-12-21"). */
const PRETTY = new Intl.DateTimeFormat("en-US", { month: "short", day: "numeric", year: "numeric", timeZone: "UTC" });

export function parseDate(text) {
  const m = typeof text === "string" ? text.match(/^(\d{4})-(\d{2})-(\d{2})$/) : null;
  if (!m) {
    return null;
  }
  const [year, month, day] = [Number(m[1]), Number(m[2]), Number(m[3])];
  const date = new Date(Date.UTC(year, month - 1, day));
  return date.getUTCMonth() === month - 1 && date.getUTCDate() === day ? date : null;
}

/** "Aug 1, 2022", or the original text if it isn't an ISO date. */
export function prettyDate(text) {
  const date = parseDate(text);
  return date ? PRETTY.format(date) : (text ?? "");
}

function todayUtc(now) {
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
  const age = now.getFullYear() - date.getUTCFullYear();
  const beforeBirthday = now.getMonth() < date.getUTCMonth()
    || (now.getMonth() === date.getUTCMonth() && now.getDate() < date.getUTCDate());
  return beforeBirthday ? age - 1 : age;
}
