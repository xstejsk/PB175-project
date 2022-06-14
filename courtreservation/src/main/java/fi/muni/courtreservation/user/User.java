package fi.muni.courtreservation.user;

import fi.muni.courtreservation.reservation.Reservation;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.SequenceGenerator;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Radim Stejskal 514102
 */
@Entity
@Table(name = "users")
@Getter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    private long id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Column(unique = true)
    private String email;
    private String userName = email;
    @NotBlank
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private Boolean locked = false;
    private Boolean enabled = false;

    public User() {
    }

    /**
     *
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @param userName email of the user
     * @param password password of the user
     * @param userRole role of the user, USER role is default
     * @param email email of the user
     */
    public User(String firstName, String lastName, String userName, String password, UserRole userRole, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.userRole = userRole;
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(
            mappedBy = "user"
    )
    private final Set<Reservation> reservations = new HashSet<>();

    @Override
    public String getUsername() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

