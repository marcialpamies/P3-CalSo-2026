package es.um.calso.dojoreservation.application.port;

import java.time.LocalDate;
import java.util.Optional;
import es.um.calso.dojoreservation.domain.model.Dojo;

public interface FindAvailableDojoInputPort {
    Optional<Dojo> find(String teamId, LocalDate trainingDate);
}
