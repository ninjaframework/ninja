package ninja;

import ninja.params.ControllerMethodInvoker;
import com.google.inject.Provider;

/**
 * The end of the filter chain
 *
 * @author James Roper
 */
class FilterChainEnd implements FilterChain {
    private final Provider<?> controllerProvider;
    private final ControllerMethodInvoker controllerMethodInvoker;

    FilterChainEnd(Provider<?> controllerProvider, ControllerMethodInvoker controllerMethodInvoker) {
        this.controllerProvider = controllerProvider;
        this.controllerMethodInvoker = controllerMethodInvoker;
    }

    @Override
    public Result next(Context context) {

        Result result;

        result = (Result) controllerMethodInvoker.invoke(controllerProvider.get(),
                context);

        if (result instanceof AsyncResult) {
            // Make sure handle async has been called
            context.handleAsync();
            Result newResult = context.controllerReturned();
            if (newResult != null) {
                result = newResult;
            }
        }

        return result;
    }
}
