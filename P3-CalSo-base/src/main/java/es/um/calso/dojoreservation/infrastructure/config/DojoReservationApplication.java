package es.um.calso.dojoreservation.infrastructure.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "es.um.calso.dojoreservation")
public class DojoReservationApplication {
    public static void main(String[] args) {
        SpringApplication.run(DojoReservationApplication.class, args);
    }
}
