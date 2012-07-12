package ninja;

import javax.inject.Provider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The end of the filter chain
 * 
 * @author James Roper
 */
class FilterChainEnd implements FilterChain {
	private final Provider<?> controllerProvider;
	private final Method method;

	FilterChainEnd(Provider<?> controllerProvider, Method method) {
		this.controllerProvider = controllerProvider;
		this.method = method;
	}

	@Override
	public Result next(Context context) {

		Result result;

		try {
			result = (Result) method.invoke(controllerProvider.get(), context);

            if (result instanceof AsyncResult) {
                // Make sure handle async has been called
                context.handleAsync();
                Result newResult = context.controllerReturned();
                if (newResult != null) {
                    result = newResult;
                }
            }

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e.getCause());
			}
		}

		return result;
	}
}
