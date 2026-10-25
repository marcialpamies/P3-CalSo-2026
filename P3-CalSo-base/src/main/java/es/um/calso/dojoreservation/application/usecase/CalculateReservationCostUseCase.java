package es.um.calso.dojoreservation.application.usecase;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import es.um.calso.dojoreservation.application.port.CalculateReservationCostInputPort;
import es.um.calso.dojoreservation.domain.model.Discount;
import es.um.calso.dojoreservation.domain.model.Dojo;
import es.um.calso.dojoreservation.domain.model.Money;
import es.um.calso.dojoreservation.domain.model.Team;
import es.um.calso.dojoreservation.domain.port.DojoRepository;
import es.um.calso.dojoreservation.domain.port.TeamRepository;
import es.um.calso.dojoreservation.domain.service.ReservationDiscountService;
import es.um.calso.dojoreservation.domain.service.ReservationPricingService;

public final class CalculateReservationCostUseCase implements CalculateReservationCostInputPort {
    private final TeamRepository teams;
    private final DojoRepository dojos;
    private final ReservationDiscountService discounts;
    private final ReservationPricingService pricing;
    private final Clock clock;

    public CalculateReservationCostUseCase(
            TeamRepository teams,
            DojoRepository dojos,
            ReservationDiscountService discounts,
            ReservationPricingService pricing,
            Clock clock) {
        this.teams = Objects.requireNonNull(teams);
        this.dojos = Objects.requireNonNull(dojos);
        this.discounts = Objects.requireNonNull(discounts);
        this.pricing = Objects.requireNonNull(pricing);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public ReservationCost calculate(String teamId, String dojoId, LocalDate trainingDate) {
        if (teamId == null || teamId.isBlank()) {
            throw new IllegalArgumentException("Team id is required");
        }
        if (dojoId == null || dojoId.isBlank()) {
            throw new IllegalArgumentException("Dojo id is required");
        }
        if (trainingDate == null || !trainingDate.isAfter(LocalDate.now(clock))) {
            throw new IllegalArgumentException("Training date must be in the future");
        }
        Team team = teams.findById(teamId.trim())
                .orElseThrow(() -> new IllegalArgumentException("Team does not exist"));
        Dojo dojo = dojos.findById(dojoId.trim())
                .orElseThrow(() -> new IllegalArgumentException("Dojo does not exist"));
        Money subtotal = pricing.calculateBaseWithWeapons(team, dojo);
        Discount discount = discounts.calculateBestDiscount(team.fighters(), trainingDate);
        Money finalPrice = pricing.calculateFinalPrice(subtotal, discount);
        Money supplement = new Money(subtotal.amount().subtract(dojo.basePrice().amount()));
        return new ReservationCost(dojo.basePrice(), supplement, subtotal, discount, finalPrice);
    }
}
