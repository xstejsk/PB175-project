package fi.muni.courtreservation.registration.token;

import fi.muni.courtreservation.customexceptions.InvalidTokenException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Radim Stejskal 514102
 */
@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    /**
     * Saves the token into the database
     * @param confirmationToken generated token for newly created account
     */
    public void saveConfirmationToken(ConfirmationToken confirmationToken){
        confirmationTokenRepository.save(confirmationToken);
    }

    /**
     *
     * @param token generated token for newly created account
     * @return ConfirmationToken object by its string representation
     * @throws fi.muni.courtreservation.customexceptions.InvalidTokenException
     */
    public Optional<ConfirmationToken> getToken(String token) throws InvalidTokenException{
        boolean exists = confirmationTokenRepository.findByToken(token).isPresent();
        if (!exists){
            throw new InvalidTokenException("given confirmation token does not exist");
        }
        return confirmationTokenRepository.findByToken(token);
    }

    /**
     * Sets the confirmation timestamp to the current datetime
     * @param token string representation of the token
     */
    public void setConfirmedAt(String token) {
        confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
