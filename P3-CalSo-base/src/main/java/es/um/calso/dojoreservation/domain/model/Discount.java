package es.um.calso.dojoreservation.domain.model;

public record Discount(int percentage) {
    public Discount {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
    }
    public Discount max(Discount other) {
        if (other == null) throw new IllegalArgumentException("Other discount is required");
        return percentage >= other.percentage ? this : other;
    }
}
