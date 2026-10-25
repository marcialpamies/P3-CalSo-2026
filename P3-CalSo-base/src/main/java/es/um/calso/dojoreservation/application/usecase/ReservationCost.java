package es.um.calso.dojoreservation.application.usecase;

import java.util.Objects;
import es.um.calso.dojoreservation.domain.model.Discount;
import es.um.calso.dojoreservation.domain.model.Money;

public record ReservationCost(
        Money basePrice,
        Money weaponSupplement,
        Money subtotal,
        Discount discount,
        Money finalPrice) {
    public ReservationCost {
        Objects.requireNonNull(basePrice, "Base price is required");
        Objects.requireNonNull(weaponSupplement, "Weapon supplement is required");
        Objects.requireNonNull(subtotal, "Subtotal is required");
        Objects.requireNonNull(discount, "Discount is required");
        Objects.requireNonNull(finalPrice, "Final price is required");
    }
}
