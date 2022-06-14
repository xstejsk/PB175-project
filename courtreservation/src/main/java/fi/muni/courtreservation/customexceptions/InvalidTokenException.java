package fi.muni.courtreservation.customexceptions;

/**
 * @author Radim Stejskal 514102
 */
public class InvalidTokenException extends Exception{
    public InvalidTokenException(String message) {
        super(message);
    }
}
