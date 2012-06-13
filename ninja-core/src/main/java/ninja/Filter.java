package ninja;

/**
 * A simple filter that can be applied to controller methods.
 * 
 * usually you use 
 * @FilterWith(MyFilter.class) where MyFilter.class is implementing 
 * this interface.
 * 
 * @FilterWith works also with multiple filter
 * @FilterWith({MyFirstFilter.class, MySecondFilter.class})
 * 
 * @author ra
 *
 */
public interface Filter {
	/**
	 * Do something with the context.
	 */
	void filter(Context context);
	
	/**
	 * Allow the filter chain to continue. 
	 * For instance if you got a plugin checking the security this
	 * filter can stop execution (and eg display an error message).
	 * 
	 * @return
	 */
	boolean continueExecution();
}
