package es.um.calso.dojoreservation.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TeamTest {
    private static final LocalDate REGISTRATION_DATE = LocalDate.of(2026, 10, 18);

    @Test
    void testGivenFiveEligibleFightersWhenCreateTeamThenFightersDefensivelyCopied() {
        List<Fighter> fighters = adultFighters(5);
        Team team = new Team("EQ-001", "Dragon", REGISTRATION_DATE, fighters, Set.of(new Weapon("Katana")));
        fighters.clear();
        assertEquals(5, team.fighters().size());
    }

    @Test
    void testGivenEligibleTeamWhenModifyExposedFightersThenModificationRejected() {
        Team team = new Team("EQ-001", "Dragon", REGISTRATION_DATE, adultFighters(5), Set.of());
        assertThrows(UnsupportedOperationException.class, () -> team.fighters().clear());
    }

    @Test
    void testGivenUnderageFighterWhenCreateTeamThenRejected() {
        List<Fighter> fighters = adultFighters(4);
        fighters.add(new Fighter("Minor", LocalDate.of(2011, 1, 1), null));
        assertThrows(IllegalArgumentException.class,
                () -> new Team("EQ-001", "Dragon", REGISTRATION_DATE, fighters, Set.of()));
    }

    @Test
    void testGivenFourFightersWhenCreateTeamThenRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Team("EQ", "Dragon", REGISTRATION_DATE, adultFighters(4), Set.of()));
    }

    @Test
    void testGivenSixFightersWhenCreateTeamThenRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Team("EQ", "Dragon", REGISTRATION_DATE, adultFighters(6), Set.of()));
    }

    @Test
    void testGivenNullFighterWhenCreateTeamThenRejected() {
        List<Fighter> fighters = adultFighters(5);
        fighters.set(0, null);
        assertThrows(IllegalArgumentException.class,
                () -> new Team("EQ", "Dragon", REGISTRATION_DATE, fighters, Set.of()));
    }

    @Test
    void testGivenNullIdWhenCreateTeamThenRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Team(null, "Dragon", REGISTRATION_DATE, adultFighters(5), Set.of()));
    }

    @Test
    void testGivenBlankIdWhenCreateTeamThenRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Team(" ", "Dragon", REGISTRATION_DATE, adultFighters(5), Set.of()));
    }

    @Test
    void testGivenNullNameWhenCreateTeamThenRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Team("EQ", null, REGISTRATION_DATE, adultFighters(5), Set.of()));
    }

    @Test
    void testGivenBlankNameWhenCreateTeamThenRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Team("EQ", " ", REGISTRATION_DATE, adultFighters(5), Set.of()));
    }

    @Test
    void testGivenNullRegistrationDateWhenCreateTeamThenRejected() {
        assertThrows(NullPointerException.class,
                () -> new Team("EQ", "Dragon", null, adultFighters(5), Set.of()));
    }

    @Test
    void testGivenNullFightersListWhenCreateTeamThenRejected() {
        assertThrows(NullPointerException.class,
                () -> new Team("EQ", "Dragon", REGISTRATION_DATE, null, Set.of()));
    }

    @Test
    void testGivenNullRequiredWeaponsWhenCreateTeamThenRejected() {
        assertThrows(NullPointerException.class,
                () -> new Team("EQ", "Dragon", REGISTRATION_DATE, adultFighters(5), null));
    }

    @Test
    void testGivenNullRequiredWeaponWhenCreateTeamThenRejected() {
        Set<Weapon> weapons = new HashSet<>();
        weapons.add(null);
        assertThrows(IllegalArgumentException.class,
                () -> new Team("EQ", "Dragon", REGISTRATION_DATE, adultFighters(5), weapons));
    }

    @Test
    void testGivenMutableWeaponsWhenCreateTeamThenDefensivelyCopied() {
        Set<Weapon> weapons = new HashSet<>(Set.of(new Weapon("Katana")));
        Team team = new Team("EQ", "Dragon", REGISTRATION_DATE, adultFighters(5), weapons);
        weapons.clear();
        assertEquals(1, team.requiredWeapons().size());
    }

    @Test
    void testGivenTeamWhenModifyExposedWeaponsThenModificationRejected() {
        Team team = new Team("EQ", "Dragon", REGISTRATION_DATE, adultFighters(5),
                Set.of(new Weapon("Katana")));
        assertThrows(UnsupportedOperationException.class, () -> team.requiredWeapons().clear());
    }

    @Test
    void testGivenIdWithSpacesWhenCreateTeamThenIdTrimmed() {
        Team team = new Team(" EQ ", "Dragon", REGISTRATION_DATE, adultFighters(5), Set.of());
        assertEquals("EQ", team.id());
    }

    @Test
    void testGivenNameWithSpacesWhenCreateTeamThenNameTrimmed() {
        Team team = new Team("EQ", " Dragon ", REGISTRATION_DATE, adultFighters(5), Set.of());
        assertEquals("Dragon", team.name());
    }

    private static List<Fighter> adultFighters(int count) {
        List<Fighter> result = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            result.add(new Fighter("Fighter " + index, LocalDate.of(2000, 1, 1), null));
        }
        return result;
    }
}
