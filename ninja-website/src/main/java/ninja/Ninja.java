package ninja;

/**
 * The main entry class for the framework.
 * 
 * The context contains the context to handle.
 * 
 * 
 * @author ra
 *
 */
public interface Ninja {

	/**
	 * Please ninja framwork - handle this context...
	 * @param context
	 */
	void invoke(ContextImpl context);

    /**
     * Start the Ninja Framework
     */
    void start();

    /**
     * Stop the Ninja Framework
     */
    void shutdown();
}