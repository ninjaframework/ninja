package ninja.lifecycle;

/**
 * Exception thrown when an error occurs when disposing of a bean
 *
 * @author James Roper
 */
public class FailedDisposeException extends LifecycleException {
    public FailedDisposeException() {
    }

    public FailedDisposeException(String message) {
        super(message);
    }

    public FailedDisposeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedDisposeException(Throwable cause) {
        super(cause);
    }
}
