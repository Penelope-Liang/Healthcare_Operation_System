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

/**
 * The API returns one row per clinic assignment; a doctor at two clinics appears twice.
 * Collapse those rows into one entry per person with all of their clinics.
 */
export function groupStaff(workers) {
  const byKey = new Map();
  for (const w of workers) {
    const key = `${w.role}/${w.id}`;
    if (!byKey.has(key)) {
      byKey.set(key, { role: w.role, id: w.id, firstName: w.firstName, lastName: w.lastName, credential: w.credential, clinics: [] });
    }
    if (w.clinicName) {
      byKey.get(key).clinics.push(w.clinicName);
    }
  }
  return [...byKey.values()];
}
