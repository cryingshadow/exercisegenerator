package exercisegenerator.util;

/**
 * An exception to be thrown if hashing cannot be executed due to a bad choice of parameters.
 * @author Thomas Stroeder
 * @version 1.0
 */
public class HashException extends Exception {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 474967907706354122L;

    /**
     * @param message The error message.
     */
    public HashException(final String message) {
        super(message);
    }

}
