package es.um.calso.dojoreservation.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import es.um.calso.dojoreservation.domain.model.Dojo;
import es.um.calso.dojoreservation.domain.model.Money;
import es.um.calso.dojoreservation.domain.model.Reservation;
import es.um.calso.dojoreservation.domain.model.Team;
import es.um.calso.dojoreservation.domain.model.Fighter;
import es.um.calso.dojoreservation.domain.model.Weapon;
import es.um.calso.dojoreservation.domain.port.DojoRepository;
import es.um.calso.dojoreservation.domain.port.ReservationRepository;
import es.um.calso.dojoreservation.domain.port.TeamRepository;
import es.um.calso.dojoreservation.infrastructure.persistence.InMemoryCatalog;

class DojoEvaluationServiceTest {
    private final InMemoryCatalog catalog = new InMemoryCatalog();
    private final TeamRepository teams = catalog::findTeamById;
    private final DojoRepository dojos = new DojoRepository() {
        @Override
        public List<Dojo> findAllInSelectionOrder() {
            return catalog.findAllInSelectionOrder();
        }

        @Override
        public Optional<Dojo> findById(String dojoId) {
            return catalog.findDojoById(dojoId);
        }
    };
    private final ReservationRepository reservations = catalog::findAll;
    private final DojoEvaluationService service =
            new DojoEvaluationService(teams, dojos, reservations);

    @Test
    void testGivenSeededDojosWhenEvaluatingThenAllDojosAreListed() {
        var rows = service.evaluate("EQ-001", LocalDate.of(2026, 11, 20),
                catalog.findDojoById("D-002").orElseThrow());
        assertEquals(3, rows.size());
    }

    @Test
    void testGivenFirstDojoBookedWhenEvaluatingThenAvailabilityIsFalse() {
        var rows = service.evaluate("EQ-001", LocalDate.of(2026, 11, 20),
                catalog.findDojoById("D-002").orElseThrow());
        assertFalse(rows.get(0).available());
    }

    @Test
    void testGivenSelectedSecondDojoWhenEvaluatingThenOnlySecondIsSelected() {
        var rows = service.evaluate("EQ-001", LocalDate.of(2026, 11, 20),
                catalog.findDojoById("D-002").orElseThrow());
        assertTrue(rows.get(1).selected());
    }

    @Test
    void testGivenIncompatibleThirdDojoWhenEvaluatingThenCompatibilityIsFalse() {
        var rows = service.evaluate("EQ-001", LocalDate.of(2026, 11, 20),
                catalog.findDojoById("D-002").orElseThrow());
        assertFalse(rows.get(2).compatible());
    }

    @Test
    void testGivenNoSelectedDojoWhenEvaluatingThenNoRowIsSelected() {
        var rows = service.evaluate("EQ-001", LocalDate.of(2026, 11, 20), null);
        assertTrue(rows.stream().noneMatch(DojoEvaluationService.DojoEvaluationRow::selected));
    }
}
