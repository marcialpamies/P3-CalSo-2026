package es.um.calso.dojoreservation.domain.port;

import java.util.List;
import java.util.Optional;
import es.um.calso.dojoreservation.domain.model.Dojo;

public interface DojoRepository {
    List<Dojo> findAllInSelectionOrder();
    Optional<Dojo> findById(String dojoId);
}
