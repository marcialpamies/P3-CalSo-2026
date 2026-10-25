package es.um.calso.dojoreservation.domain.port;

import java.util.List;
import es.um.calso.dojoreservation.domain.model.Reservation;

public interface ReservationRepository {
    List<Reservation> findAll();
}
