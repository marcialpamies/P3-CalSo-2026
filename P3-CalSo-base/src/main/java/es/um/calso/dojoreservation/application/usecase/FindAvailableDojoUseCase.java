package es.um.calso.dojoreservation.application.usecase;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import es.um.calso.dojoreservation.application.port.FindAvailableDojoInputPort;
import es.um.calso.dojoreservation.domain.model.Dojo;
import es.um.calso.dojoreservation.domain.model.Team;
import es.um.calso.dojoreservation.domain.port.DojoRepository;
import es.um.calso.dojoreservation.domain.port.ReservationRepository;
import es.um.calso.dojoreservation.domain.port.TeamRepository;
import es.um.calso.dojoreservation.domain.service.DojoSearchService;

public final class FindAvailableDojoUseCase implements FindAvailableDojoInputPort {
    private final TeamRepository teams;
    private final DojoRepository dojos;
    private final ReservationRepository reservations;
    private final DojoSearchService search;
    private final Clock clock;

    public FindAvailableDojoUseCase(
            TeamRepository teams,
            DojoRepository dojos,
            ReservationRepository reservations,
            DojoSearchService search,
            Clock clock) {
        this.teams = Objects.requireNonNull(teams);
        this.dojos = Objects.requireNonNull(dojos);
        this.reservations = Objects.requireNonNull(reservations);
        this.search = Objects.requireNonNull(search);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public Optional<Dojo> find(String teamId, LocalDate trainingDate) {
        if (teamId == null || teamId.isBlank()) {
            throw new IllegalArgumentException("Team id is required");
        }
        if (trainingDate == null || !trainingDate.isAfter(LocalDate.now(clock))) {
            throw new IllegalArgumentException("Training date must be in the future");
        }
        Team team = teams.findById(teamId.trim())
                .orElseThrow(() -> new IllegalArgumentException("Team does not exist"));
        return search.findFirstAvailableDojo(
                dojos.findAllInSelectionOrder(),
                team.requiredWeapons(),
                reservations.findAll(),
                trainingDate);
    }
}
