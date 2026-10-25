package es.um.calso.dojoreservation.domain.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;

public final class Fighter {
    private final String name;
    private final LocalDate birthDate;
    private final UnemploymentPeriod unemployment;

    public Fighter(String name, LocalDate birthDate, UnemploymentPeriod unemployment) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        this.name = name.trim();
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date is required");
        if (unemployment != null && unemployment.startDate().isBefore(birthDate.plusYears(16))) {
            throw new IllegalArgumentException("Unemployment cannot start before age 16");
        }
        this.unemployment = unemployment;
    }
    public String name() { return name; }
    public LocalDate birthDate() { return birthDate; }
    public Optional<UnemploymentPeriod> unemployment() { return Optional.ofNullable(unemployment); }
    public int ageAt(LocalDate referenceDate) {
        Objects.requireNonNull(referenceDate, "Reference date is required");
        if (referenceDate.isBefore(birthDate)) throw new IllegalArgumentException("Date precedes birth");
        return Period.between(birthDate, referenceDate).getYears();
    }
    public boolean canRegisterAt(LocalDate registrationDate) { return ageAt(registrationDate) >= 16; }
}
