package fi.muni.courtreservation.court;

import fi.muni.courtreservation.reservation.Reservation;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Radim Stejskal 514102
 */
@Entity(name = "courts")
@Getter
@Setter
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "court_sequence")
    @SequenceGenerator(
            name = "court_sequence",
            sequenceName = "court_sequence",
            allocationSize = 1)
    private Long id;
    private boolean isAvailable = true;
    @OneToMany(
            mappedBy = "courtId"
    )
    private final Set<Reservation> reservations = new HashSet<>();
}
