package es.um.calso.dojoreservation.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DojoTest {
    @Test
    void testGivenMutableWeaponsWhenCreateDojoThenInventoryIsDefensivelyCopied() {
        Set<Weapon> weapons = new HashSet<>(Set.of(new Weapon("Katana")));
        Dojo dojo = new Dojo("D-001", "North", Money.euros("40"), weapons);
        weapons.clear();
        assertEquals(1, dojo.weapons().size());
    }

    @Test
    void testGivenDojoWhenModifyExposedWeaponsThenModificationRejected() {
        Dojo dojo = new Dojo("D-001", "North", Money.euros("40"), Set.of(new Weapon("Katana")));
        assertThrows(UnsupportedOperationException.class, () -> dojo.weapons().clear());
    }

    @Test
    void testGivenNullDojoIdWhenCreateDojoThenRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Dojo(null, "North", Money.euros("40"), Set.of()));
    }

    @Test
    void testGivenBlankDojoIdWhenCreateDojoThenRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Dojo("  ", "North", Money.euros("40"), Set.of()));
    }

    @Test
    void testGivenNullDojoNameWhenCreateDojoThenRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Dojo("D", null, Money.euros("40"), Set.of()));
    }

    @Test
    void testGivenBlankDojoNameWhenCreateDojoThenRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Dojo("D", "  ", Money.euros("40"), Set.of()));
    }

    @Test
    void testGivenMissingBasePriceWhenCreateDojoThenRejected() {
        assertThrows(NullPointerException.class, () -> new Dojo("D", "North", null, Set.of()));
    }

    @Test
    void testGivenMissingInventoryWhenCreateDojoThenRejected() {
        assertThrows(NullPointerException.class, () -> new Dojo("D", "North", Money.euros("40"), null));
    }

    @Test
    void testGivenNullWeaponWhenCreateDojoThenRejected() {
        Set<Weapon> inventory = new HashSet<>();
        inventory.add(null);
        assertThrows(IllegalArgumentException.class, () -> new Dojo("D", "North", Money.euros("40"), inventory));
    }

    @Test
    void testGivenDojoIdWithSpacesWhenCreateDojoThenIdentifierTrimmed() {
        Dojo dojo = new Dojo(" D-1 ", "North", Money.euros("40"), Set.of());
        assertEquals("D-1", dojo.id());
    }

    @Test
    void testGivenDojoNameWithSpacesWhenCreateDojoThenNameTrimmed() {
        Dojo dojo = new Dojo("D-1", " North ", Money.euros("40"), Set.of());
        assertEquals("North", dojo.name());
    }

    @Test
    void testGivenImmutableNonemptyInventoryWhenCreateDojoThenAccepted() {
        Dojo dojo = new Dojo("D", "North", Money.euros("40"), Set.of(new Weapon("Katana")));
        assertEquals(1, dojo.weapons().size());
    }
}
