package fi.muni.courtreservation.court;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Radim Stejskal 514102
 */
public interface CourtRepository extends JpaRepository<Court, Long> {
}
