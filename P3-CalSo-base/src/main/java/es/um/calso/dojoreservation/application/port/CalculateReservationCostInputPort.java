package es.um.calso.dojoreservation.application.port;

import java.time.LocalDate;
import es.um.calso.dojoreservation.application.usecase.ReservationCost;

public interface CalculateReservationCostInputPort {
    ReservationCost calculate(String teamId, String dojoId, LocalDate trainingDate);
}
