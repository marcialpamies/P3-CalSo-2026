package es.um.calso.dojoreservation.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import es.um.calso.dojoreservation.domain.model.Dojo;
import es.um.calso.dojoreservation.domain.model.Fighter;
import es.um.calso.dojoreservation.domain.model.Money;
import es.um.calso.dojoreservation.domain.model.Reservation;
import es.um.calso.dojoreservation.domain.model.Team;
import es.um.calso.dojoreservation.domain.model.UnemploymentPeriod;
import es.um.calso.dojoreservation.domain.model.Weapon;

public final class InMemoryCatalog {
    private final List<Team> teams;
    private final List<Dojo> dojos;
    private final List<Reservation> reservations;

    public InMemoryCatalog() {
        Weapon katana = new Weapon("Katana");
        Weapon bokken = new Weapon("Bokken");
        Weapon bo = new Weapon("Bo");
        Fighter young = new Fighter("Hana", LocalDate.of(2009, 11, 20), null);
        Fighter unemployed = new Fighter("Akira", LocalDate.of(2000, 1, 1),
                new UnemploymentPeriod(LocalDate.of(2024, 11, 20)));
        Fighter adult = new Fighter("Ren", LocalDate.of(1998, 6, 1), null);
        Fighter youthBoundary = new Fighter("Nora", LocalDate.of(2009, 11, 20), null);
        Fighter twoYearBoundary = new Fighter("Izan", LocalDate.of(2000, 1, 1),
                new UnemploymentPeriod(LocalDate.of(2025, 11, 20)));
        Fighter threeYearBoundary = new Fighter("Lea", LocalDate.of(2000, 1, 1),
                new UnemploymentPeriod(LocalDate.of(2024, 11, 20)));
        this.teams = List.of(
                new Team("EQ-001", "Equipo Dragón", LocalDate.of(2026, 9, 1),
                        List.of(young, unemployed, adult, adult, adult), Set.of(katana, bokken)),
                new Team("EQ-002", "Equipo Tigre", LocalDate.of(2026, 9, 1),
                        List.of(adult, adult, adult, adult, adult), Set.of(bo)),
                new Team("EQ-003", "Equipo Frontera Edad", LocalDate.of(2026, 9, 1),
                        List.of(youthBoundary, adult, adult, adult, adult), Set.of(bo)),
                new Team("EQ-004", "Equipo Frontera Dos Años", LocalDate.of(2026, 9, 1),
                        List.of(twoYearBoundary, adult, adult, adult, adult), Set.of(bo)),
                new Team("EQ-005", "Equipo Frontera Tres Años", LocalDate.of(2026, 9, 1),
                        List.of(threeYearBoundary, adult, adult, adult, adult), Set.of(bo)));
        this.dojos = List.of(
                new Dojo("D-001", "Dojo Norte", Money.euros("40"), Set.of(katana, bokken)),
                new Dojo("D-002", "Dojo Centro", Money.euros("50"), Set.of(katana, bokken, bo)),
                new Dojo("D-003", "Dojo Sur", Money.euros("35"), Set.of(bo)));
        this.reservations = List.of(
                new Reservation("D-001", LocalDate.of(2026, 11, 20)),
                new Reservation("D-001", LocalDate.of(2027, 11, 20)));
    }

    public Optional<Team> findTeamById(String teamId) {
        return teams.stream().filter(team -> team.id().equals(teamId)).findFirst();
    }

    public List<Dojo> findAllInSelectionOrder() {
        return dojos;
    }

    public Optional<Dojo> findDojoById(String dojoId) {
        return dojos.stream().filter(dojo -> dojo.id().equals(dojoId)).findFirst();
    }

    public List<Reservation> findAll() {
        return reservations;
    }
}
