package ninja.lifecycle;

import com.google.inject.ImplementedBy;

/**
 * Responsible for starting/stopping the application
 *
 * @author James Roper
 */
@ImplementedBy(LifecycleServiceImpl.class)
public interface LifecycleService {
    /**
     * Start the application
     */
    void start();

    /**
     * Stop the application
     */
    void stop();

    /**
     * Whether the application is started
     *
     * @return True if the application is started
     */
    public boolean isStarted();

    /**
     * Get the state of the lifecycle
     *
     * @return The state
     */
    public State getState();

    /**
     * Get the time that the service has been up for
     *
     * @return The time that the service has been up for
     */
    public long getUpTime();
}
