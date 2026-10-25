package es.um.calso.dojoreservation.domain.model;

import java.util.Objects;
import java.util.Set;

public record Dojo(String id, String name, Money basePrice, Set<Weapon> weapons) {
    public Dojo {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Dojo id is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Dojo name is required");
        Objects.requireNonNull(basePrice, "Base price is required");
        Objects.requireNonNull(weapons, "Inventory is required");
        if (weapons.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("Null weapon");
        id = id.trim(); name = name.trim(); weapons = Set.copyOf(weapons);
    }
}
