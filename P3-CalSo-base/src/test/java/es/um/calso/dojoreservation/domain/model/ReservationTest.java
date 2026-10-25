package es.um.calso.dojoreservation.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ReservationTest {
    @Test
    void testGivenDojoAndDateWhenCreateReservationThenDojoIdPreserved() {
        Reservation reservation = new Reservation("D-001", LocalDate.of(2026, 11, 20));
        assertEquals("D-001", reservation.dojoId());
    }

    @Test
    void testGivenDojoAndDateWhenCreateReservationThenDatePreserved() {
        Reservation reservation = new Reservation("D-001", LocalDate.of(2026, 11, 20));
        assertEquals(LocalDate.of(2026, 11, 20), reservation.trainingDate());
    }

    @Test
    void testGivenNullIdWhenCreateReservationThenRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation(null, LocalDate.of(2026, 11, 20)));
    }

    @Test
    void testGivenBlankIdWhenCreateReservationThenRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Reservation("  ", LocalDate.of(2026, 11, 20)));
    }

    @Test
    void testGivenNullDateWhenCreateReservationThenRejected() {
        assertThrows(NullPointerException.class, () -> new Reservation("D", null));
    }

    @Test
    void testGivenIdWithSpacesWhenCreateReservationThenTrimmed() {
        Reservation reservation = new Reservation(" D-1 ", LocalDate.of(2026, 11, 20));
        assertEquals("D-1", reservation.dojoId());
    }
}
