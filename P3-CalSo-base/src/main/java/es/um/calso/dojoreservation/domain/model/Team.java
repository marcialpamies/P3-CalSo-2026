package es.um.calso.dojoreservation.domain.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.Objects;

public record Team(String id, String name, LocalDate registrationDate,
                   List<Fighter> fighters, Set<Weapon> requiredWeapons) {
    public Team {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Team id is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Team name is required");
        Objects.requireNonNull(registrationDate, "Registration date is required");
        Objects.requireNonNull(fighters, "Fighters are required");
        Objects.requireNonNull(requiredWeapons, "Weapons are required");
        if (fighters.size() != 5 || fighters.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("A team must contain exactly five fighters");
        }
        if (fighters.stream().anyMatch(f -> !f.canRegisterAt(registrationDate))) {
            throw new IllegalArgumentException("Every fighter must be at least 16 at registration");
        }
        if (requiredWeapons.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Null weapon");
        }
        id = id.trim(); name = name.trim();
        fighters = List.copyOf(fighters);
        requiredWeapons = Set.copyOf(requiredWeapons);
    }
}
