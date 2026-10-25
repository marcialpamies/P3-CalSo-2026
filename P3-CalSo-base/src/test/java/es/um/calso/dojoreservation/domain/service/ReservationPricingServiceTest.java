package es.um.calso.dojoreservation.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import es.um.calso.dojoreservation.domain.model.*;

class ReservationPricingServiceTest {
    private final ReservationPricingService service = new ReservationPricingService();

    @Test
    void testGivenThreeRequiredWeaponsWhenCalculateSubtotalThenAddsFifteenEuros() {
        Team team = team(Set.of(new Weapon("Katana"), new Weapon("Bokken"), new Weapon("Nunchaku")));
        Dojo dojo = new Dojo("D1", "North", Money.euros("40"), team.requiredWeapons());
        Money result = service.calculateBaseWithWeapons(team, dojo);
        assertEquals(Money.euros("55.00"), result);
    }

    @Test
    void testGivenZeroRequiredWeaponsWhenCalculateSubtotalThenBaseUnchanged() {
        Team team = team(Set.of());
        Dojo dojo = new Dojo("D1", "North", Money.euros("40"), Set.of());
        Money result = service.calculateBaseWithWeapons(team, dojo);
        assertEquals(Money.euros("40.00"), result);
    }

    @Test
    void testGivenTwentyPercentDiscountWhenCalculateFinalThenCorrectAmount() {
        Money result = service.calculateFinalPrice(Money.euros("55"), new Discount(20));
        assertEquals(Money.euros("44.00"), result);
    }

    @Test
    void testGivenNullTeamWhenCalculateSubtotalThenRejected() {
        Dojo dojo = new Dojo("D1", "North", Money.euros("40"), Set.of());
        assertThrows(NullPointerException.class, () -> service.calculateBaseWithWeapons(null, dojo));
    }

    private Team team(Set<Weapon> weapons) {
        Fighter fighter = new Fighter("Adult", LocalDate.of(2000, 1, 1), null);
        return new Team("T1", "Dragons", LocalDate.of(2026, 1, 1),
                List.of(fighter, fighter, fighter, fighter, fighter), weapons);
    }
}
