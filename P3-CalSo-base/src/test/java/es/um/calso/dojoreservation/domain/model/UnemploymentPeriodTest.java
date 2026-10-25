package es.um.calso.dojoreservation.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class UnemploymentPeriodTest {
    @Test
    void testGivenDayBeforeSecondAnniversaryWhenCalculateCompleteYearsThenOne() {
        UnemploymentPeriod period = new UnemploymentPeriod(LocalDate.of(2024, 11, 20));
        assertEquals(1, period.completeYearsAt(LocalDate.of(2026, 11, 19)));
    }

    @Test
    void testGivenSecondAnniversaryWhenCalculateCompleteYearsThenTwo() {
        UnemploymentPeriod period = new UnemploymentPeriod(LocalDate.of(2024, 11, 20));
        assertEquals(2, period.completeYearsAt(LocalDate.of(2026, 11, 20)));
    }

    @Test
    void testGivenDayBeforeFirstAnniversaryWhenCalculateCompleteYearsThenZero() {
        UnemploymentPeriod period = new UnemploymentPeriod(LocalDate.of(2023, 11, 20));
        assertEquals(0, period.completeYearsAt(LocalDate.of(2024, 11, 19)));
    }

    @Test
    void testGivenFirstAnniversaryWhenCalculateCompleteYearsThenOne() {
        UnemploymentPeriod period = new UnemploymentPeriod(LocalDate.of(2023, 11, 20));
        assertEquals(1, period.completeYearsAt(LocalDate.of(2024, 11, 20)));
    }

    @Test
    void testGivenDayBeforeThirdAnniversaryWhenCalculateCompleteYearsThenTwo() {
        UnemploymentPeriod period = new UnemploymentPeriod(LocalDate.of(2023, 11, 20));
        assertEquals(2, period.completeYearsAt(LocalDate.of(2026, 11, 19)));
    }

    @Test
    void testGivenThirdAnniversaryWhenCalculateCompleteYearsThenThree() {
        UnemploymentPeriod period = new UnemploymentPeriod(LocalDate.of(2023, 11, 20));
        assertEquals(3, period.completeYearsAt(LocalDate.of(2026, 11, 20)));
    }

    @Test
    void testGivenDateBeforeUnemploymentWhenCalculateCompleteYearsThenRejected() {
        UnemploymentPeriod period = new UnemploymentPeriod(LocalDate.of(2025, 1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> period.completeYearsAt(LocalDate.of(2024, 12, 31)));
    }

    @Test
    void testGivenNullStartDateWhenCreatePeriodThenRejected() {
        assertThrows(NullPointerException.class, () -> new UnemploymentPeriod(null));
    }

    @Test
    void testGivenNullReferenceDateWhenCalculateCompleteYearsThenRejected() {
        UnemploymentPeriod period = new UnemploymentPeriod(LocalDate.of(2025, 1, 1));
        assertThrows(NullPointerException.class, () -> period.completeYearsAt(null));
    }
}
