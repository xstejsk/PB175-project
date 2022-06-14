package fi.muni.courtreservation.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author Radim Stejskal 514102
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    void enableUser(String email);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(long id);

    @Query("SELECT u FROM User u WHERE u.userName LIKE %?1%")
    List<User> findAllByKeyword(String keyword);
}
