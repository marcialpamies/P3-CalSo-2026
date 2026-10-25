package es.um.calso.dojoreservation.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InMemoryCatalogTest {
    private final InMemoryCatalog catalog = new InMemoryCatalog();

    @Test
    void testGivenKnownTeamWhenFindByIdThenTeamFound() {
        assertEquals("EQ-001", catalog.findTeamById("EQ-001").orElseThrow().id());
    }

    @Test
    void testGivenUnknownTeamWhenFindByIdThenEmpty() {
        assertTrue(catalog.findTeamById("EQ-999").isEmpty());
    }

    @Test
    void testGivenKnownDojoWhenFindByIdThenDojoFound() {
        assertEquals("D-002", catalog.findDojoById("D-002").orElseThrow().id());
    }

    @Test
    void testGivenSeededDojosWhenFindAllThenStableOrder() {
        assertEquals("D-001", catalog.findAllInSelectionOrder().get(0).id());
    }

    @Test
    void testGivenSeededReservationWhenFindAllThenExpectedDate() {
        assertEquals(LocalDate.of(2026, 11, 20), catalog.findAll().get(0).trainingDate());
    }
}
