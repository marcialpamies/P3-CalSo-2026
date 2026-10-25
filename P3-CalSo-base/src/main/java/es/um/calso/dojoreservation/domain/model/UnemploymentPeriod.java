package es.um.calso.dojoreservation.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record UnemploymentPeriod(LocalDate startDate) {
    public UnemploymentPeriod {
        Objects.requireNonNull(startDate, "Unemployment start date is required");
    }
    public long completeYearsAt(LocalDate referenceDate) {
        Objects.requireNonNull(referenceDate, "Reference date is required");
        if (referenceDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Reference date precedes unemployment");
        }
        return ChronoUnit.YEARS.between(startDate, referenceDate);
    }
}
