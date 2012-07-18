package ninja;

/**
 * Exception thrown when an error in route configuration is found
 *
 * @author James Roper
 */
public class RoutingException extends RuntimeException {
    public RoutingException() {
    }

    public RoutingException(String message) {
        super(message);
    }

    public RoutingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoutingException(Throwable cause) {
        super(cause);
    }
}
