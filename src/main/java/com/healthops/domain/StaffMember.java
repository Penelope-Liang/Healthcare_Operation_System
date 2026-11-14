package com.healthops.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** One nurse or doctor with every clinic they are assigned to (the schema allows several). */
public record StaffMember(Role role, String id, String firstName, String lastName, String credential,
                          List<String> clinics) {

    public String fullName() {
        return firstName + " " + lastName;
    }

    /** Collapses one-row-per-assignment results into one entry per person, keeping order. */
    public static List<StaffMember> group(List<Worker> rows) {
        Map<String, StaffMember> byKey = new LinkedHashMap<>();
        for (Worker w : rows) {
            StaffMember member = byKey.computeIfAbsent(w.role().key() + "/" + w.id(), k ->
                    new StaffMember(w.role(), w.id(), w.firstName(), w.lastName(), w.credential(), new ArrayList<>()));
            if (w.clinicName() != null) {
                member.clinics().add(w.clinicName());
            }
        }
        return List.copyOf(byKey.values());
    }
}
