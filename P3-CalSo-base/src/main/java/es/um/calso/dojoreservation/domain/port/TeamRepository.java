package es.um.calso.dojoreservation.domain.port;

import java.util.Optional;
import es.um.calso.dojoreservation.domain.model.Team;

public interface TeamRepository {
    Optional<Team> findById(String teamId);
}
