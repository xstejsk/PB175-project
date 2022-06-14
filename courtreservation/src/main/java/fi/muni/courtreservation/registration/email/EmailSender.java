package fi.muni.courtreservation.registration.email;

/**
 * @author Radim Stejskal 514102
 */
public interface EmailSender {
    void send(String to, String email);
}
