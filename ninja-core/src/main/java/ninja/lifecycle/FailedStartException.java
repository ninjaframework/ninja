package ninja.lifecycle;

/**
 * Exception thrown when an error occurs while starting a bean
 *
 * @author James Roper
 */
public class FailedStartException extends LifecycleException {
    public FailedStartException() {
    }

    public FailedStartException(String message) {
        super(message);
    }

    public FailedStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedStartException(Throwable cause) {
        super(cause);
    }
}
