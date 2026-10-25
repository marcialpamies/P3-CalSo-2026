package es.um.calso.dojoreservation.domain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import es.um.calso.dojoreservation.domain.model.Discount;
import es.um.calso.dojoreservation.domain.model.Fighter;
import es.um.calso.dojoreservation.domain.model.UnemploymentPeriod;

public final class ReservationDiscountService {

    public Optional<Fighter> findYoungestFighter(
            List<Fighter> fighters,
            LocalDate referenceDate) {
        if (fighters == null) {
            throw new NullPointerException("Fighters are required");
        }
        if (referenceDate == null) {
            throw new NullPointerException("Reference date is required");
        }

        Fighter youngest = null;
        for (Fighter fighter : fighters) {
            if (fighter == null) {
                throw new NullPointerException("Null fighter");
            }
            if (youngest == null || fighter.birthDate().isBefore(youngest.birthDate())) {
                youngest = fighter;
            }
        }
        return Optional.ofNullable(youngest);
    }

    public Discount calculateBestDiscount(
            List<Fighter> fighters,
            LocalDate trainingDate) {
        if (fighters == null) {
            throw new NullPointerException("Fighters are required");
        }
        if (trainingDate == null) {
            throw new NullPointerException("Training date is required");
        }

        int bestPercentage = 0;
        Optional<Fighter> youngest = findYoungestFighter(fighters, trainingDate);
        if (youngest.isPresent()) {
            int age = youngest.orElseThrow().ageAt(trainingDate);
            if (age < 18) {
                bestPercentage = 15;
            }
        }

        for (Fighter fighter : fighters) {
            if (fighter == null) {
                throw new NullPointerException("Null fighter");
            }
            Optional<UnemploymentPeriod> period = fighter.unemployment();
            if (period.isPresent()) {
                long completeYears = period.orElseThrow().completeYearsAt(trainingDate);
                int unemploymentPercentage = 0;
                if (completeYears >= 3) {
                    unemploymentPercentage = 30;
                } else if (completeYears > 2) {
                    unemploymentPercentage = 20;
                } else if (completeYears >= 1) {
                    unemploymentPercentage = 10;
                }
                if (unemploymentPercentage > bestPercentage) {
                    bestPercentage = unemploymentPercentage;
                }
            }
        }
        return new Discount(bestPercentage);
    }
}
