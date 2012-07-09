package ninja.lifecycle;

/**
 * Exception thrown when an error occurs in the lifecycle
 *
 * @author James Roper
 */
public class LifecycleException extends RuntimeException {
    public LifecycleException() {
    }

    public LifecycleException(String message) {
        super(message);
    }

    public LifecycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public LifecycleException(Throwable cause) {
        super(cause);
    }
}
