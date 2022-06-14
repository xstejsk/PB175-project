package fi.muni.courtreservation.reservation;

import fi.muni.courtreservation.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Radim Stejskal 514102
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT tc.id FROM courts tc " +
            "WHERE tc.id NOT IN " +
            "(SELECT r.courtId FROM reservations r " +
            "WHERE ?1 < r.ending AND r.start < ?2 AND r.id <> ?3)")
    List<Long> findFreeCourtForReservation(LocalDateTime start,
                                           LocalDateTime end, Long reservationId);

    List<Reservation> findReservationsByUserOrderByStartAsc(User user);

    @Query("SELECT r FROM reservations r WHERE r.user.id = ?1 AND r.ending >=" +
            " CURRENT_DATE ORDER BY r.start ASC")
    List<Reservation> findActiveReservationsByUser(Long userId);

    @Query("SELECT r FROM reservations r WHERE r.user.userName LIKE %?1%")
    List<Reservation> findAllByKeyword(String keyword);

    Optional<Reservation> findReservationById(Long id);

    void deleteAllByCourtId(Long id);
}
