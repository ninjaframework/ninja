package ninja.lifecycle;

/**
 * States that are possible for a service.
 */
public enum State {
    /**
     * The service is currently starting
     */
    STARTING,

    /**
     * The service has been started and is therefore "running"
     */
    STARTED,

    /**
     * The Service is currently stopping
     */
    STOPPING,

    /**
     * The service is stopped, not runnig.
     */
    STOPPED
}
