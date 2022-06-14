package fi.muni.courtreservation.reservation;

import fi.muni.courtreservation.court.CourtService;
import fi.muni.courtreservation.customexceptions.InvalidReservationException;
import fi.muni.courtreservation.user.User;
import fi.muni.courtreservation.user.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Radim Stejskal 514102
 */
@Service
@AllArgsConstructor
public class ReservationService {
    @Autowired
    private Clock clock;
    private final ReservationRepository reservationRepository;
    private final CourtService courtService;
    private static final int PAGE_SIZE = 15;
    private static final int OPENING_HOUR = 8;
    private static final int CLOSING_HOUR = 18;
    private static final Integer[] ALLOWED_DURATIONS = new Integer[]{1, 2};
    private static final DayOfWeek[] OPEN_DAYS = new DayOfWeek[]{
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY};

    /**
     *
     * @param pageNumber number of the page to be shown
     * @return A single page containing @PAGE_SIZE number of all reservations
     */
    public Page<Reservation> getPageOfAllReservations(int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE);
        return reservationRepository.findAll(pageable);
    }

    /**
     *
     * @param reservation reservation to check
     * @return true if reservation starts and ends within allowed time, false otherwise
     */
    private boolean timeAndDurationIsValid(Reservation reservation){
        return  reservation.getStart().getHour() >= OPENING_HOUR &&
                reservation.getEnding().getHour() <= CLOSING_HOUR &&
        Arrays.asList(ALLOWED_DURATIONS).contains(reservation.getDurationHrs());

    }

    /**
     *
     * @param localDateTime start of the reservation
     * @return true if reservation day is open day, false otherwise
     */

    private boolean isOpenDay(LocalDateTime localDateTime){
        DayOfWeek day = localDateTime.getDayOfWeek();
        return Arrays.asList(OPEN_DAYS).contains(day);
    }
    /**
     *
     * @param keyword a substring of user's email
     * @return all reservations matching with the keyword
     */
    public List<Reservation> getReservationsByKeyword(String keyword){
        return reservationRepository.findAllByKeyword(keyword);
    }

    /**
     *
     * @param user User object
     * @return returns reservations belonging to the user having start >= now
     */
    public List<Reservation> getActiveReservationsByUser(User user){
        return reservationRepository.findActiveReservationsByUser(user.getId());
    }

    /**
     * Automatically assigns a court that is free in the selected time of the reservation
     * @param reservation Reservations object
     * @throws InvalidReservationException thrown when no court is free in the selected time
     * of the reservation
     */
    public void assignFreeCourt(Reservation reservation) throws InvalidReservationException {
        LocalDateTime start = reservation.getStart();
        LocalDateTime end = reservation.getEnding();
        Long reservationId = reservation.getId();

        List<Long> freeCourts = reservationRepository.findFreeCourtForReservation(start, end, reservationId);
        if (freeCourts.isEmpty()){
            throw new InvalidReservationException("No court is free in selected time");
        }
        reservation.setCourtId(Collections.min(freeCourts));
    }

    /**
     * removes a reservation with the given id from the database
     * @param id id of the reservation to be deleted from the database
     */
    public void deleteReservation(Long id, User user) throws AccessDeniedException {
        if (!reservationRepository.existsById(id)){
            throw new IllegalStateException("reservation with given id " + id + "does not exist");
        }

        if (!userOwnsReservation(user, id) && user.getUserRole() != UserRole.ADMIN){
            throw new AccessDeniedException("invalid reservation id");
        }

        reservationRepository.deleteById(id);
    }


    /**
     * Frees the court of given id of all reservations
     * @param id court id
     */
    public void deleteReservationsByCourtId(Long id){
        if (!courtService.existsById(id)){
            throw new IllegalStateException("court with given id " + id + "does not exist");
        }
        reservationRepository.deleteById(id);
    }

    /**
     *
     * @param id id of the reservation
     * @return Reservation object with the given id
     * @throws IllegalArgumentException thrown when no such reservation exists
     */
    public Reservation getReservationById(Long id) throws IllegalArgumentException{
        Optional<Reservation> reservation = reservationRepository.findReservationById(id);
        if (reservation.isPresent()){
            return reservation.get();
        }
        throw new IllegalArgumentException("reservation with given ID does not exist");
    }

    /**
     * Saves the reservation object to the database
     * @param reservation reservation object
     * @throws InvalidReservationException thrown when reservation's start is < now
     */
    @Transactional
    public void saveReservation(Reservation reservation) throws InvalidReservationException {
        LocalDateTime.now(clock);
        reservation.setStart(reservation.getStartDate().atTime(reservation.getStartHrs(), 0));
        if (reservation.getStart().isBefore(LocalDateTime.now(clock))){
            throw new InvalidReservationException("Cannot create reservation in the past");
        }
        reservation.setEnding(reservation.getStart().plusHours(reservation.getDurationHrs()));
        assignFreeCourt(reservation);
        if (!isOpenDay(reservation.getStart())){
            throw new InvalidReservationException("reservation cannot start on the weekend");
        }
        if (!timeAndDurationIsValid(reservation)){
            throw new InvalidReservationException("reservation time is invalid");
        }
        reservationRepository.save(reservation);
    }

    /**
     *
     * @param user User object
     * @param reservationId id of the reservation
     * @return true if the reservation with given id belongs to the given user
     */
    public boolean userOwnsReservation(User user, long reservationId){
        List<Reservation> reservations = reservationRepository.findReservationsByUserOrderByStartAsc(user);
        return reservations.stream().map(Reservation::getId).toList().contains(reservationId);
    }

}
