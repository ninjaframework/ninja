package ninja;

/**
 * Renderables can be returned inside a result.
 * 
 * Renderables are responsible for finalizing the headers before anything
 * is written to the output streams.
 * 
 * context.finalizeHeaders(result) is your friend.
 * 
 * It is not done automatically as you may want to change the status of the response,
 * the return type and so on...
 * 
 * 
 * @author rbauer
 *
 */
public interface Renderable {
	
	void render(Context context, Result result) throws Exception;

}
