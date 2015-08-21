package salt.samczsun.exception;

/**
 * Thrown when invalid credentials are given to log in
 *
 * @author samczsun
 */
@SuppressWarnings("serial")
public class InvalidCredentialsException extends SkypeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, String html) {
        super(message + "\n" + html);
    }
}
