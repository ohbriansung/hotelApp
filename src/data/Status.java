package data;


/**
 * Creates a Status enum type for tracking errors. Each Status enum type
 * will use the ordinal as the error code, and store a message describing
 * the error.
 */
public enum Status {

	/*
	 * Creates several Status enum types. The Status name and message is
	 * given in the NAME(message) format below. The Status ordinal is
	 * determined by its position in the list. (For example, OK is the
	 * first element, and will have ordinal 0.)
	 */

    OK("No errors occured."),
    ERROR("Unknown error occurred."),
    MISSING_CONFIG("Unable to find configuration file."),
    MISSING_VALUES("Missing values in configuration file."),
    CONNECTION_FAILED("Failed to establish a database connection."),
    CREATE_FAILED("Failed to create necessary tables."),
    INVALID_LOGIN("Invalid username and/or password."),
    INVALID_USER("User does not exist."),
    DUPLICATE_USER("User with that username already exists."),
    SQL_EXCEPTION("Unable to execute SQL statement."),
    USERNAME_OK("Username checked."),
    USERNAME_NOT_GOOD("Username contains invalid character(s)."),
    PASSWORD_OK("Password checked."),
    PASSWORD_NOT_GOOD("Password not good.");

    private final String message;

    private Status(String message) {
        this.message = message;
    }

    /**
     * Return the message.
     * @return
     */
    public String message() {
        return message;
    }

    /**
     * Override the toString method to customize the return String.
     * @return String
     */
    @Override
    public String toString() {
        return this.message;
    }
}