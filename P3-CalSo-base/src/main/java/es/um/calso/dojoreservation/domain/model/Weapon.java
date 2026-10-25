package es.um.calso.dojoreservation.domain.model;

public record Weapon(String name) {
    public Weapon {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Weapon name is required");
        }
        name = name.trim();
    }
}
