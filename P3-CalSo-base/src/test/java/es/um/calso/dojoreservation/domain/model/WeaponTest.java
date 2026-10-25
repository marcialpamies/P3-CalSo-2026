package es.um.calso.dojoreservation.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class WeaponTest {
    @Test
    void testGivenValidNameWithSpacesWhenCreateWeaponThenTrimmed() {
        Weapon weapon = new Weapon(" Katana ");
        assertEquals("Katana", weapon.name());
    }

    @Test
    void testGivenNullNameWhenCreateWeaponThenRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Weapon(null));
    }

    @Test
    void testGivenBlankNameWhenCreateWeaponThenRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Weapon("  "));
    }

    @Test
    void testGivenSameNameWhenCompareWeaponsThenEqual() {
        assertEquals(new Weapon("Katana"), new Weapon(" Katana "));
    }
}
