package com.healthops;

import com.healthops.web.DateDisplay;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DateDisplayTest {

    private final DateDisplay dates = new DateDisplay();

    @Test
    void parsesEveryLegacyFormat() {
        assertThat(dates.parse("2021-12-21")).isEqualTo(LocalDate.of(2021, 12, 21));
        assertThat(dates.parse("01 AUG 2022")).isEqualTo(LocalDate.of(2022, 8, 1));
        assertThat(dates.parse("01 JULY 2021")).isEqualTo(LocalDate.of(2021, 7, 1));
        assertThat(dates.parse("not a date")).isNull();
        assertThat(dates.parse(null)).isNull();
    }

    @Test
    void prettyFallsBackToOriginalText() {
        assertThat(dates.pretty("01 AUG 2022")).isEqualTo("Aug 1, 2022");
        assertThat(dates.pretty("someday")).isEqualTo("someday");
    }

    @Test
    void detectsExpiredDates() {
        assertThat(dates.isPast("01 AUG 2022")).isTrue();
        assertThat(dates.isPast(LocalDate.now().plusYears(1).toString())).isFalse();
    }
}
