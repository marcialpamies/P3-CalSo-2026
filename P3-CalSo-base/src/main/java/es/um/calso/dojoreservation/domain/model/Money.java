package es.um.calso.dojoreservation.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount) {
    public Money {
        Objects.requireNonNull(amount, "Amount is required");
        if (amount.signum() < 0) throw new IllegalArgumentException("Amount cannot be negative");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
    public static Money euros(String amount) { return new Money(new BigDecimal(amount)); }
    public Money plus(Money other) {
        return new Money(amount.add(Objects.requireNonNull(other).amount));
    }
    public Money after(Discount discount) {
        Objects.requireNonNull(discount);
        return new Money(amount.multiply(BigDecimal.valueOf(100L - discount.percentage()))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }
}
