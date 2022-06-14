package fi.muni.courtreservation.reservation;

import fi.muni.courtreservation.court.CourtService;
import fi.muni.courtreservation.customexceptions.InvalidReservationException;
import fi.muni.courtreservation.user.User;
import fi.muni.courtreservation.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private final static LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2022, Month.MARCH,
            10, 14, 0);

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CourtService courtService;

    private ReservationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        Clock fixedClock = Clock.fixed(LOCAL_DATE_TIME.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        underTest = new ReservationService(fixedClock, reservationRepository,
                courtService);

    }

    @Test
    void shouldSaveReservations() throws InvalidReservationException {
        List<Long> courts = new ArrayList<>();
        courts.add(1L);
        when(reservationRepository.findFreeCourtForReservation(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(courts);
        List<Reservation> reservationsToPass = new ArrayList<>();
        reservationsToPass.add(new Reservation());
        reservationsToPass.add(new Reservation());
        reservationsToPass.add(new Reservation());
        reservationsToPass.get(0).setStartDate(LocalDate.of(2022, Month.MAY, 12));
        reservationsToPass.get(0).setStartHrs(8);
        reservationsToPass.get(0).setDurationHrs(2);

        reservationsToPass.get(1).setStartDate(LocalDate.of(2022, Month.MAY, 12));
        reservationsToPass.get(1).setStartHrs(9);
        reservationsToPass.get(1).setDurationHrs(1);

        reservationsToPass.get(2).setStartDate(LocalDate.of(2022, Month.MAY, 20));
        reservationsToPass.get(2).setStartHrs(10);
        reservationsToPass.get(2).setDurationHrs(2);
        
        for (Reservation resv : reservationsToPass) {
            underTest.saveReservation(resv);
        }

        ArgumentCaptor<Reservation> reservationArgumentCaptor =
                ArgumentCaptor.forClass(Reservation.class);

        verify(reservationRepository, times(3)).save(reservationArgumentCaptor.capture());

        List<Reservation> capturedReservations = reservationArgumentCaptor.getAllValues();
        assertThat((capturedReservations.containsAll(reservationsToPass))).isTrue();
    }

    @Test
    void shouldNotSaveReservations() {
        List<Long> courts = new ArrayList<>();
        courts.add(1L);
        when(reservationRepository.findFreeCourtForReservation(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(courts);
        List<Reservation> reservationsToFail = new ArrayList<>();
        reservationsToFail.add(new Reservation());
        reservationsToFail.add(new Reservation());
        reservationsToFail.add(new Reservation());
        reservationsToFail.add(new Reservation());
        reservationsToFail.get(0).setStartDate(LocalDate.of(2000, Month.MAY, 12));
        reservationsToFail.get(0).setStartHrs(10);
        reservationsToFail.get(0).setDurationHrs(1);

        reservationsToFail.get(1).setStartDate(LocalDate.of(2022, Month.MAY, 18));
        reservationsToFail.get(1).setStartHrs(10);
        reservationsToFail.get(1).setDurationHrs(0);

        reservationsToFail.get(2).setStartDate(LocalDate.of(2022, Month.MAY, 14));
        reservationsToFail.get(2).setStartHrs(8);
        reservationsToFail.get(2).setDurationHrs(1);

        reservationsToFail.get(3).setStartDate(LocalDate.of(2022, Month.MAY, 20));
        reservationsToFail.get(3).setStartHrs(18);
        reservationsToFail.get(3).setDurationHrs(-9);

        reservationsToFail.forEach(resv -> assertThatThrownBy(() ->
                underTest.saveReservation(resv)).isInstanceOf(InvalidReservationException.class));
    }

    @Test
    void shouldCancelReservations(){
        Reservation resv1 = new Reservation();
        resv1.setId(1L);
        Reservation resv2 = new Reservation();
        resv2.setId(2L);
        Reservation resv3 = new Reservation();
        resv3.setId(3L);
        Reservation resv4 = new Reservation();
        resv4.setId(4L);

        List<Reservation> usr1Resvs = new ArrayList<>(Arrays.asList(resv1, resv2));
        List<Reservation> usr2Resvs = new ArrayList<>(Arrays.asList(resv3, resv4));
        List<Reservation> usr3Resvs = new ArrayList<>();

        User usr1 = new User("usr1",
                "",
                "",
                "",
                UserRole.USER,
                "");

        User usr2 = new User("usr1",
                "",
                "",
                "",
                UserRole.USER,
                "");

        User usr3 = new User("usr1",
                "",
                "",
                "",
                UserRole.ADMIN,
                "");

        when(reservationRepository.findReservationsByUserOrderByStartAsc(usr1)).
                thenReturn(usr1Resvs);

        when(reservationRepository.findReservationsByUserOrderByStartAsc(usr2)).
                thenReturn(usr2Resvs);

        when(reservationRepository.findReservationsByUserOrderByStartAsc(usr3)).
                thenReturn(usr3Resvs);

        when(reservationRepository.existsById(Mockito.any())).thenReturn(true);

        try {
            underTest.deleteReservation(1L, usr1);
            underTest.deleteReservation(1L, usr3);
            underTest.deleteReservation(4L, usr3);
            underTest.deleteReservation(4L, usr2);
        }catch (Exception ignored){ // should never be thrown
            assertThat(true).isFalse();
        }

        ArgumentCaptor<Long> reservationArgumentCaptor =
                ArgumentCaptor.forClass(Long.class);

        verify(reservationRepository, times(4)).deleteById(
                reservationArgumentCaptor.capture());

        List<Long> capturedIds = reservationArgumentCaptor.getAllValues();
        assertThat(capturedIds).hasSameElementsAs(Arrays.asList(1L, 1L, 4L, 4L));
    }

    @Test
    void shouldNotCancelReservations(){
        Reservation resv1 = new Reservation();
        resv1.setId(1L);
        Reservation resv2 = new Reservation();
        resv2.setId(2L);
        Reservation resv3 = new Reservation();
        resv3.setId(3L);
        Reservation resv4 = new Reservation();
        resv4.setId(4L);

        List<Reservation> usr1Resvs = new ArrayList<>(Arrays.asList(resv1, resv2));
        List<Reservation> usr2Resvs = new ArrayList<>(Arrays.asList(resv3, resv4));

        User usr1 = new User("usr1",
                "",
                "",
                "",
                UserRole.USER,
                "");

        User usr2 = new User("usr1",
                "",
                "",
                "",
                UserRole.USER,
                "");

        when(reservationRepository.findReservationsByUserOrderByStartAsc(usr2)).
                thenReturn(usr2Resvs);
        when(reservationRepository.findReservationsByUserOrderByStartAsc(usr1)).
                thenReturn(usr1Resvs);

        when(reservationRepository.existsById(Mockito.any())).thenReturn(true);

        assertThatThrownBy(() -> underTest.deleteReservation(1L, usr2)).isInstanceOf(
                AccessDeniedException.class);
        assertThatThrownBy(() -> underTest.deleteReservation(2L, usr2)).isInstanceOf(
                AccessDeniedException.class);
        assertThatThrownBy(() -> underTest.deleteReservation(3L, usr1)).isInstanceOf(
                AccessDeniedException.class);
    }
}