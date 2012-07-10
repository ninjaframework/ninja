package ninja.async;

/**
 * The strategy for async handling
 */
public interface AsyncStrategy {
    void handleAsync();
    void controllerReturned();
    void requestComplete();
}
