package ninja;

/**
 * A filter chain
 */
public interface FilterChain {
    /**
     * Pass the request to the next filter
     *
     * @param context The context for the request
     */
    Result next(Context context);
}
