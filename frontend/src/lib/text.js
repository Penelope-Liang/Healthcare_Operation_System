/** Case-insensitive "does any of these values contain the query" check used by every search box. */
export function matchesQuery(query, ...values) {
  const q = query.trim().toLowerCase();
  if (!q) {
    return true;
  }
  return values.some((v) => v != null && String(v).toLowerCase().includes(q));
}

export function fullName(person) {
  return `${person.firstName} ${person.lastName}`;
}

/** "RN, BSN , rn" -> ["RN", "BSN"]: trims, drops blanks and case-insensitive duplicates. */
export function splitList(text) {
  const seen = new Set();
  return String(text ?? "")
    .split(",")
    .map((v) => v.trim())
    .filter((v) => v && !seen.has(v.toLowerCase()) && seen.add(v.toLowerCase()));
}

export function plural(count, word) {
  return `${count} ${word}${count === 1 ? "" : "s"}`;
}
