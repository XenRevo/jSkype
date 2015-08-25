package salt.samczsun.exception;

/**
 * Represents any exception that may occur while using this API
 *
 * @author samczsun
 */
@SuppressWarnings("serial")
public class SkypeException extends Exception {
    public SkypeException() {
        super();
    }

    public SkypeException(String message) {
        super(message);
    }
}
