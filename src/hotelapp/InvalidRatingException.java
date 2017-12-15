package hotelapp;

/**
 * Custom exception class for entering invalid hotel rating
 */
public class InvalidRatingException extends Exception {

    /**
     * Constructor for class InvalidRatingException to pass the exception message.
     * @param message
     */
    public InvalidRatingException(String message) {
        super(message);
        System.out.println("The value of the hotel rating is out of range");
    }

}
