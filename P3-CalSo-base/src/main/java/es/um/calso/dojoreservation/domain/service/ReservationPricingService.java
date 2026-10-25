package es.um.calso.dojoreservation.domain.service;

import java.math.BigDecimal;
import java.util.Objects;
import es.um.calso.dojoreservation.domain.model.Discount;
import es.um.calso.dojoreservation.domain.model.Dojo;
import es.um.calso.dojoreservation.domain.model.Money;
import es.um.calso.dojoreservation.domain.model.Team;

public final class ReservationPricingService {
    private static final Money PRICE_PER_WEAPON = Money.euros("5.00");

    public Money calculateBaseWithWeapons(Team team, Dojo dojo) {
        Objects.requireNonNull(team, "Team is required");
        Objects.requireNonNull(dojo, "Dojo is required");
        Money extra = new Money(PRICE_PER_WEAPON.amount().multiply(
                BigDecimal.valueOf(team.requiredWeapons().size())));
        return dojo.basePrice().plus(extra);
    }

    public Money calculateFinalPrice(Money subtotal, Discount discount) {
        Objects.requireNonNull(subtotal, "Subtotal is required");
        Objects.requireNonNull(discount, "Discount is required");
        return subtotal.after(discount);
    }
}
