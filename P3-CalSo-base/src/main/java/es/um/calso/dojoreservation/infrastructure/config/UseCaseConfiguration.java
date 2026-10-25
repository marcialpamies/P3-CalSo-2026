package es.um.calso.dojoreservation.infrastructure.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import es.um.calso.dojoreservation.application.port.CalculateReservationCostInputPort;
import es.um.calso.dojoreservation.application.port.FindAvailableDojoInputPort;
import es.um.calso.dojoreservation.application.usecase.CalculateReservationCostUseCase;
import es.um.calso.dojoreservation.application.usecase.FindAvailableDojoUseCase;
import es.um.calso.dojoreservation.domain.model.Dojo;
import es.um.calso.dojoreservation.domain.port.DojoRepository;
import es.um.calso.dojoreservation.domain.port.ReservationRepository;
import es.um.calso.dojoreservation.domain.port.TeamRepository;
import es.um.calso.dojoreservation.domain.service.DojoSearchService;
import es.um.calso.dojoreservation.domain.service.ReservationDiscountService;
import es.um.calso.dojoreservation.domain.service.ReservationPricingService;
import es.um.calso.dojoreservation.infrastructure.persistence.InMemoryCatalog;

import java.util.List;
import java.util.Optional;

@Configuration
public class UseCaseConfiguration {
    @Bean
    @Profile("!e2e")
    public Clock productionClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    @Profile("e2e")
    public Clock e2eClock() {
        return Clock.fixed(Instant.parse("2026-10-20T10:00:00Z"), ZoneId.of("Europe/Madrid"));
    }

    @Bean
    public InMemoryCatalog catalog() {
        return new InMemoryCatalog();
    }

    @Bean
    public TeamRepository teamRepository(InMemoryCatalog catalog) {
        return catalog::findTeamById;
    }

    @Bean
    public DojoRepository dojoRepository(InMemoryCatalog catalog) {
        return new DojoRepository() {
            @Override
            public List<Dojo> findAllInSelectionOrder() {
                return catalog.findAllInSelectionOrder();
            }

            @Override
            public Optional<Dojo> findById(String dojoId) {
                return catalog.findDojoById(dojoId);
            }
        };
    }

    @Bean
    public ReservationRepository reservationRepository(InMemoryCatalog catalog) {
        return catalog::findAll;
    }

    @Bean
    public FindAvailableDojoInputPort findAvailableDojo(
            TeamRepository teams, DojoRepository dojos, ReservationRepository reservations, Clock clock) {
        return new FindAvailableDojoUseCase(teams, dojos, reservations,
                new DojoSearchService(), clock);
    }

    @Bean
    public CalculateReservationCostInputPort calculateReservationCost(
            TeamRepository teams, DojoRepository dojos, Clock clock) {
        return new CalculateReservationCostUseCase(teams, dojos,
                new ReservationDiscountService(), new ReservationPricingService(),
                clock);
    }
}
