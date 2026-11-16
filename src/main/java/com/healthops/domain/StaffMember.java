package com.healthops.domain;

import java.util.List;

/** A nurse or doctor with all of their credentials and clinic assignments. */
public record StaffMember(String id, Role role, String firstName, String lastName,
                          List<String> credentials, List<String> clinics) {
}
