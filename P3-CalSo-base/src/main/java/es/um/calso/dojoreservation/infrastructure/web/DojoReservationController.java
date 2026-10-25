package es.um.calso.dojoreservation.infrastructure.web;

import java.time.LocalDate;
import java.time.Clock;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import es.um.calso.dojoreservation.application.port.CalculateReservationCostInputPort;
import es.um.calso.dojoreservation.application.port.FindAvailableDojoInputPort;

@Controller
public class DojoReservationController {
    private final FindAvailableDojoInputPort search;
    private final CalculateReservationCostInputPort cost;
    private final DojoEvaluationService evaluation;
    private final Clock clock;

    public DojoReservationController(
            FindAvailableDojoInputPort search, CalculateReservationCostInputPort cost,
            DojoEvaluationService evaluation, Clock clock) {
        this.search = search;
        this.cost = cost;
        this.evaluation = evaluation;
        this.clock = clock;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dojos";
    }

    @GetMapping("/dojos")
    public String dojos(@RequestParam(name = "teamId", defaultValue = "EQ-001") String teamId,
            @RequestParam(name = "trainingDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate trainingDate, Model model) {
        LocalDate date = trainingDate == null ? LocalDate.now(clock).plusDays(1) : trainingDate;
        model.addAttribute("teamId", teamId);
        model.addAttribute("trainingDate", date);
        try {
            var selected = search.find(teamId, date).orElse(null);
            model.addAttribute("dojo", selected);
            model.addAttribute("dojoRows", evaluation.evaluate(teamId, date, selected));
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
        }
        return "dojos";
    }

    @GetMapping("/coste")
    public String coste(@RequestParam(name = "teamId", defaultValue = "EQ-001") String teamId,
            @RequestParam(name = "dojoId", defaultValue = "D-002") String dojoId,
            @RequestParam(name = "trainingDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate trainingDate, Model model) {
        LocalDate date = trainingDate == null ? LocalDate.now(clock).plusDays(1) : trainingDate;
        model.addAttribute("teamId", teamId);
        model.addAttribute("dojoId", dojoId);
        model.addAttribute("trainingDate", date);
        try {
            model.addAttribute("cost", cost.calculate(teamId, dojoId, date));
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
        }
        return "coste";
    }
}
