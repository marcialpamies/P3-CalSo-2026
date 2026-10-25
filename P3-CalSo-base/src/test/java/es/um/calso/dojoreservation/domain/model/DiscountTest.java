package es.um.calso.dojoreservation.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DiscountTest {
    @Test
    void testGivenFifteenAndTwentyPercentWhenSelectMaximumThenTwenty() {
        Discount result = new Discount(15).max(new Discount(20));
        assertEquals(new Discount(20), result);
    }

    @Test
    void testGivenPercentageAboveHundredWhenCreateDiscountThenRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Discount(101));
    }

    @Test
    void testGivenPercentageBelowZeroWhenCreateDiscountThenRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Discount(-1));
    }

    @Test
    void testGivenZeroPercentWhenCreateDiscountThenAccepted() {
        Discount discount = new Discount(0);
        assertEquals(0, discount.percentage());
    }

    @Test
    void testGivenHundredPercentWhenCreateDiscountThenAccepted() {
        Discount discount = new Discount(100);
        assertEquals(100, discount.percentage());
    }

    @Test
    void testGivenHigherFirstDiscountWhenSelectMaximumThenFirstPreserved() {
        Discount first = new Discount(30);
        assertSame(first, first.max(new Discount(10)));
    }

    @Test
    void testGivenEqualDiscountsWhenSelectMaximumThenFirstPreserved() {
        Discount first = new Discount(15);
        assertSame(first, first.max(new Discount(15)));
    }

    @Test
    void testGivenNullOtherWhenSelectMaximumThenRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Discount(10).max(null));
    }
}
