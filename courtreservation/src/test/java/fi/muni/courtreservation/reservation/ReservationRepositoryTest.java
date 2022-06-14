package fi.muni.courtreservation.reservation;

import fi.muni.courtreservation.court.Court;
import fi.muni.courtreservation.court.CourtRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository underTest;
    @Autowired
    private CourtRepository courtRepository;

    @AfterEach
    void tearDown(){
        underTest.deleteAll();
    }

    @Test
    void findFreeCourtForReservation() {
        addCourts();
        addReservations();
        Reservation testReservation = new Reservation();
        testReservation.setId(4);
        testReservation
                .setStart(LocalDateTime.of(2022, Month.MARCH,
                        12, 8, 0));
        testReservation
                .setEnding(LocalDateTime.of(2022, Month.MARCH,
                        12, 10, 0));

        assertThat(underTest.findFreeCourtForReservation(testReservation.getStart(),
                testReservation.getEnding(),
                testReservation.getId())).isEmpty();

        testReservation
                .setStart(LocalDateTime.of(2022, Month.MARCH,
                        12, 10, 0));
        testReservation
                .setEnding(LocalDateTime.of(2022, Month.MARCH,
                        12, 11, 0));

        assertThat(underTest.findFreeCourtForReservation(testReservation.getStart(),
                testReservation.getEnding(),
                testReservation.getId())).containsExactly(2L);

        testReservation
                .setStart(LocalDateTime.of(2022, Month.MARCH,
                        18, 10, 0));
        testReservation
                .setEnding(LocalDateTime.of(2022, Month.MARCH,
                        18, 12, 0));

        assertThat(underTest.findFreeCourtForReservation(testReservation.getStart(),
                testReservation.getEnding(),
                testReservation.getId())).containsExactlyInAnyOrder(1L, 2L);

        testReservation
                .setStart(LocalDateTime.of(2022, Month.MARCH,
                        12, 8, 0));
        testReservation
                .setEnding(LocalDateTime.of(2022, Month.MARCH,
                        12, 9, 0));
        testReservation.setId(1);

        assertThat(underTest.findFreeCourtForReservation(testReservation.getStart(),
                testReservation.getEnding(),
                testReservation.getId())).containsExactly(1L);

        testReservation.setId(2);
        testReservation
                .setStart(LocalDateTime.of(2022, Month.MARCH,
                        12, 8, 0));
        testReservation
                .setEnding(LocalDateTime.of(2022, Month.MARCH,
                        12, 9, 0));

        assertThat(underTest.findFreeCourtForReservation(testReservation.getStart(),
                testReservation.getEnding(),
                testReservation.getId())).containsExactly(2L);

        testReservation.setId(4);
        testReservation
                .setStart(LocalDateTime.of(2022, Month.MARCH,
                        12, 8, 0));
        testReservation
                .setEnding(LocalDateTime.of(2022, Month.MARCH,
                        12, 9, 0));

        assertThat(underTest.findFreeCourtForReservation(testReservation.getStart(),
                testReservation.getEnding(),
                testReservation.getId())).isEmpty();

        testReservation.setId(3);
        testReservation
                .setStart(LocalDateTime.of(2022, Month.MARCH,
                        12, 10, 0));
        testReservation
                .setEnding(LocalDateTime.of(2022, Month.MARCH,
                        12, 12, 0));

        assertThat(underTest.findFreeCourtForReservation(testReservation.getStart(),
                testReservation.getEnding(),
                testReservation.getId())).containsExactlyInAnyOrder(1L, 2L);
    }

    void addReservations(){
        Reservation reservation1 = new Reservation();
        reservation1
                .setStart(LocalDateTime.of(2022, Month.MARCH, 12, 8, 0));
        reservation1
                .setEnding(LocalDateTime.of(2022, Month.MARCH,
                        12, 9, 0));
        reservation1.setCourtId(1);

        Reservation reservation2 = new Reservation();
        reservation2
                .setStart(LocalDateTime.of(2022, Month.MARCH, 12, 8, 0));
        reservation2
                .setEnding(LocalDateTime.of(2022, Month.MARCH,
                        12, 9, 0));
        reservation2.setCourtId(2);

        Reservation reservation3 = new Reservation();
        reservation3
                .setStart(LocalDateTime.of(2022, Month.MARCH, 12, 10, 0));
        reservation3
                .setEnding(LocalDateTime.of(2022, Month.MARCH, 12,
                        12, 0));
        reservation3.setCourtId(1);
        underTest.save(reservation1);
        underTest.save(reservation2);
        underTest.save(reservation3);
    }

    void addCourts(){
        courtRepository.save(new Court());
        courtRepository.save(new Court());
    }
}