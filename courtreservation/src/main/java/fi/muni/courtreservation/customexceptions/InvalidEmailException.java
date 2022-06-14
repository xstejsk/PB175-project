package fi.muni.courtreservation.customexceptions;

/**
 * @author Radim Stejskal 514102
 */
public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException(String message) {
        super(message);
    }
}
