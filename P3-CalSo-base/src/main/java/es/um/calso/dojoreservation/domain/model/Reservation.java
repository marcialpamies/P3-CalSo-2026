package es.um.calso.dojoreservation.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public record Reservation(String dojoId, LocalDate trainingDate) {
    public Reservation {
        if (dojoId == null || dojoId.isBlank()) throw new IllegalArgumentException("Dojo id is required");
        Objects.requireNonNull(trainingDate, "Training date is required");
        dojoId = dojoId.trim();
    }
}
