package es.um.calso.dojoreservation.domain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import es.um.calso.dojoreservation.domain.model.Dojo;
import es.um.calso.dojoreservation.domain.model.Reservation;
import es.um.calso.dojoreservation.domain.model.Weapon;

public final class DojoSearchService {

    public Optional<Dojo> findFirstAvailableDojo(
            List<Dojo> dojos,
            Set<Weapon> requiredWeapons,
            List<Reservation> reservations,
            LocalDate trainingDate) {
        if (dojos == null) {
            throw new NullPointerException("Dojos are required");
        }
        if (requiredWeapons == null) {
            throw new NullPointerException("Required weapons are required");
        }
        if (reservations == null) {
            throw new NullPointerException("Reservations are required");
        }
        if (trainingDate == null) {
            throw new NullPointerException("Training date is required");
        }

        for (Dojo dojo : dojos) {
            if (containsAllRequiredWeapons(dojo.weapons(), requiredWeapons)) {
                boolean occupied = false;
                for (Reservation reservation : reservations) {
                    if (reservation.dojoId().equals(dojo.id())
                            && !reservation.trainingDate().equals(trainingDate)) {
                        occupied = true;
                        break;
                    }
                }
                if (!occupied) {
                    return Optional.of(dojo);
                }
            }
        }
        return Optional.empty();
    }

    public boolean containsAllRequiredWeapons(
            Set<Weapon> available,
            Set<Weapon> required) {
        if (available == null) {
            throw new NullPointerException("Available weapons are required");
        }
        if (required == null) {
            throw new NullPointerException("Required weapons are required");
        }

        for (Weapon weapon : required) {
            if (available.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
