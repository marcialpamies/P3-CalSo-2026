package es.um.calso.dojoreservation.infrastructure.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.um.calso.dojoreservation.domain.model.Dojo;
import es.um.calso.dojoreservation.domain.model.Reservation;
import es.um.calso.dojoreservation.domain.model.Team;
import es.um.calso.dojoreservation.domain.port.DojoRepository;
import es.um.calso.dojoreservation.domain.port.ReservationRepository;
import es.um.calso.dojoreservation.domain.port.TeamRepository;

/**
 * Read-only presentation projection. The domain search remains responsible
 * for selecting the first eligible dojo.
 */
@Service
public class DojoEvaluationService {
    private final TeamRepository teams;
    private final DojoRepository dojos;
    private final ReservationRepository reservations;

    public DojoEvaluationService(TeamRepository teams, DojoRepository dojos,
            ReservationRepository reservations) {
        this.teams = Objects.requireNonNull(teams);
        this.dojos = Objects.requireNonNull(dojos);
        this.reservations = Objects.requireNonNull(reservations);
    }

    public List<DojoEvaluationRow> evaluate(String teamId, LocalDate trainingDate, Dojo selected) {
        Objects.requireNonNull(teamId, "Team id is required");
        Objects.requireNonNull(trainingDate, "Training date is required");
        Team team = teams.findById(teamId.trim())
                .orElseThrow(() -> new IllegalArgumentException("Team does not exist"));
        List<Reservation> bookings = reservations.findAll();
        return dojos.findAllInSelectionOrder().stream().map(dojo -> {
            boolean compatible = dojo.weapons().containsAll(team.requiredWeapons());
            boolean available = bookings.stream().noneMatch(reservation ->
                    reservation.dojoId().equals(dojo.id())
                            && reservation.trainingDate().equals(trainingDate));
            boolean chosen = selected != null && selected.id().equals(dojo.id());
            return new DojoEvaluationRow(dojo.id(), dojo.name(), compatible, available, chosen);
        }).toList();
    }

    public record DojoEvaluationRow(
            String id, String name, boolean compatible, boolean available, boolean selected) {
    }
}
