package fi.muni.courtreservation.registration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

/**
 * contains details from the registration form view template
 * @author Radim Stejskal 514102
 */
@NoArgsConstructor
@Getter
@Setter
public class RegistrationRequest {

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String password;
    @NotBlank
    @Column(unique = true)
    private String email;

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
