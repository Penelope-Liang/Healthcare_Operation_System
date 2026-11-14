package com.healthops.web;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Dates in the legacy schema are stored as text in mixed formats ("2021-12-21", "01 AUG 2022",
 * "01 JULY 2021"). This bean, exposed to templates as {@code @dates}, parses any of them and
 * renders a single readable format.
 */
@Component("dates")
public class DateDisplay {

    private static final List<DateTimeFormatter> FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            legacy("dd MMM yyyy"),
            legacy("dd MMMM yyyy"),
            legacy("d MMM yyyy"),
            legacy("d MMMM yyyy"));

    private static final DateTimeFormatter PRETTY = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    private static DateTimeFormatter legacy(String pattern) {
        return new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(pattern).toFormatter(Locale.ENGLISH);
    }

    public LocalDate parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        for (DateTimeFormatter format : FORMATS) {
            try {
                return LocalDate.parse(text.trim(), format);
            } catch (DateTimeParseException ignored) {
                // try the next format
            }
        }
        return null;
    }

    /** A readable date, or the original text if it cannot be parsed. */
    public String pretty(String text) {
        LocalDate date = parse(text);
        return date == null ? (text == null ? "" : text) : PRETTY.format(date);
    }

    public boolean isPast(String text) {
        LocalDate date = parse(text);
        return date != null && date.isBefore(LocalDate.now());
    }

    /** Whole years between the date and today, for patient ages. */
    public Integer age(String text) {
        LocalDate date = parse(text);
        return date == null ? null : java.time.Period.between(date, LocalDate.now()).getYears();
    }
}
