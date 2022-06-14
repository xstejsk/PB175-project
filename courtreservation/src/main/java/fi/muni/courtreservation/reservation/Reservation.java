package fi.muni.courtreservation.reservation;

import fi.muni.courtreservation.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Radim Stejskal 514102
 */
@Entity(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation implements Serializable {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE, generator = "reservation_sequence"
    )
    @SequenceGenerator(
            name = "reservation_sequence",
            sequenceName = "reservation_sequence",
            allocationSize = 1
    )
    private long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd'At'HH:mm")
    private LocalDateTime start;
    @Transient
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @Transient
    private int startHrs;
    @Transient
    private int durationHrs;
    @DateTimeFormat(pattern = "yyyy-MM-dd'At'HH:mm")
    private LocalDateTime ending;
    private long courtId;
    @ManyToOne
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id == that.id && start.equals(that.start) && ending.equals(that.ending) && user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, ending, user);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + ending +
                ", user=" + user +
                '}';
    }
}
