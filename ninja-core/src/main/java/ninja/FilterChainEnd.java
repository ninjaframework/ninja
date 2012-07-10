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
    public void next(Context context) {
        try {
            method.invoke(controllerProvider.get(), context);
            context.controllerReturned();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }
}
