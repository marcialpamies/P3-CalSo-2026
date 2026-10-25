package es.um.calso.dojoreservation.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MoneyTest {
    @Test
    void testGivenFiftyFiveEurosAndFifteenPercentWhenApplyDiscountThenFortySixSeventyFive() {
        Money result = Money.euros("55").after(new Discount(15));
        assertEquals(Money.euros("46.75"), result);
    }

    @Test
    void testGivenNegativeAmountWhenCreateMoneyThenRejected() {
        assertThrows(IllegalArgumentException.class, () -> Money.euros("-1"));
    }

    @Test
    void testGivenTwoAmountsWhenAddThenSumWithTwoDecimals() {
        Money result = Money.euros("10.20").plus(Money.euros("5.30"));
        assertEquals(Money.euros("15.50"), result);
    }

    @Test
    void testGivenThreeDecimalAmountWhenCreateThenHalfUpRounding() {
        assertEquals(Money.euros("1.01"), Money.euros("1.005"));
    }

    @Test
    void testGivenZeroDiscountWhenApplyThenAmountUnchanged() {
        Money result = Money.euros("10").after(new Discount(0));
        assertEquals(Money.euros("10.00"), result);
    }

    @Test
    void testGivenFullDiscountWhenApplyThenZeroAmount() {
        Money result = Money.euros("10").after(new Discount(100));
        assertEquals(Money.euros("0.00"), result);
    }

    @Test
    void testGivenNullAmountWhenCreateMoneyThenRejected() {
        assertThrows(NullPointerException.class, () -> new Money(null));
    }

    @Test
    void testGivenNullOperandWhenAddMoneyThenRejected() {
        assertThrows(NullPointerException.class, () -> Money.euros("10").plus(null));
    }

    @Test
    void testGivenNullDiscountWhenApplyThenRejected() {
        assertThrows(NullPointerException.class, () -> Money.euros("10").after(null));
    }
}
