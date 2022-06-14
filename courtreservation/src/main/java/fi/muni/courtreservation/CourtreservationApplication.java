package fi.muni.courtreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.time.Clock;

/**
 * main application class
 * @author Radim Stejskal 514102
 */
@SpringBootApplication
@Controller
public class CourtreservationApplication {

	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}

	public static void main(String[] args) {
		SpringApplication.run(CourtreservationApplication.class, args);
	}
}
