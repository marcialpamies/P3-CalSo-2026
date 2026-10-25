package es.um.calso.dojoreservation.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class FighterTest {
    @Test
    void testGivenFighterTurning16TodayWhenRegisterThenAllowed() {
        Fighter fighter = new Fighter("Ana", LocalDate.of(2010, 10, 18), null);
        assertTrue(fighter.canRegisterAt(LocalDate.of(2026, 10, 18)));
    }

    @Test
    void testGivenFighterDayBefore16WhenRegisterThenRejected() {
        Fighter fighter = new Fighter("Ana", LocalDate.of(2010, 10, 19), null);
        assertFalse(fighter.canRegisterAt(LocalDate.of(2026, 10, 18)));
    }

    @Test
    void testGivenUnemploymentBeforeAge16WhenCreateFighterThenRejected() {
        UnemploymentPeriod unemployment = new UnemploymentPeriod(LocalDate.of(2025, 1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Fighter("Ana", LocalDate.of(2010, 10, 18), unemployment));
    }

    @Test
    void testGivenDayBeforeEighteenthBirthdayWhenCalculateAgeThenSeventeen() {
        Fighter fighter = new Fighter("Ana", LocalDate.of(2008, 11, 20), null);
        assertEquals(17, fighter.ageAt(LocalDate.of(2026, 11, 19)));
    }

    @Test
    void testGivenEighteenthBirthdayWhenCalculateAgeThenEighteen() {
        Fighter fighter = new Fighter("Ana", LocalDate.of(2008, 11, 20), null);
        assertEquals(18, fighter.ageAt(LocalDate.of(2026, 11, 20)));
    }

    @Test
    void testGivenNameWithSpacesWhenCreateFighterThenNameTrimmed() {
        Fighter fighter = new Fighter(" Ana ", LocalDate.of(2010, 10, 18),
                new UnemploymentPeriod(LocalDate.of(2026, 10, 18)));
        assertEquals("Ana", fighter.name());
    }

    @Test
    void testGivenUnemploymentStartingAtSixteenWhenCreateFighterThenPeriodRetained() {
        UnemploymentPeriod period = new UnemploymentPeriod(LocalDate.of(2026, 10, 18));
        Fighter fighter = new Fighter("Ana", LocalDate.of(2010, 10, 18), period);
        assertEquals(period, fighter.unemployment().orElseThrow());
    }

    @Test
    void testGivenEmployedFighterWhenReadUnemploymentThenEmpty() {
        Fighter fighter = new Fighter("Ana", LocalDate.of(2000, 1, 1), null);
        assertTrue(fighter.unemployment().isEmpty());
    }

    @Test
    void testGivenNullNameWhenCreateFighterThenRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Fighter(null, LocalDate.of(2000, 1, 1), null));
    }

    @Test
    void testGivenBlankNameWhenCreateFighterThenRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Fighter(" ", LocalDate.of(2000, 1, 1), null));
    }

    @Test
    void testGivenNullBirthDateWhenCreateFighterThenRejected() {
        assertThrows(NullPointerException.class, () -> new Fighter("Ana", null, null));
    }

    @Test
    void testGivenDateBeforeBirthWhenCalculateAgeThenRejected() {
        Fighter fighter = new Fighter("Ana", LocalDate.of(2010, 10, 18), null);
        assertThrows(IllegalArgumentException.class,
                () -> fighter.ageAt(LocalDate.of(2010, 10, 17)));
    }

    @Test
    void testGivenNullReferenceDateWhenCalculateAgeThenRejected() {
        Fighter fighter = new Fighter("Ana", LocalDate.of(2010, 10, 18), null);
        assertThrows(NullPointerException.class, () -> fighter.ageAt(null));
    }
}
