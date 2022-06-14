package fi.muni.courtreservation.customexceptions;

/**
 * @author Radim Stejskal 514102
 */
public class InvalidReservationException extends Exception{
    public InvalidReservationException(String errorMessage) {
        super(errorMessage);
    }
}
