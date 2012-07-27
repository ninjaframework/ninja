package ninja;

/**
 * A simple filter that can be applied to controller methods or while classes.
 * 
 * usually you use 
 * <code>@FilterWith(MyFilter.class)</code> where MyFilter.class is implementing
 * this interface.
 * 
 * <code>@FilterWith</code> works also with multiple filter
 * <code>@FilterWith({MyFirstFilter.class, MySecondFilter.class})</code>
 * 
 * @author ra
 *
 */
public interface Filter {
	/**
	 * Filter the request.  Filters should invoke the filterChain.nextFilter() method if they wish the request to
     * proceed.
     *
     * @param filterChain The filter chain
     * @param context The context
	 */
	Result filter(FilterChain filterChain, Context context);
}
