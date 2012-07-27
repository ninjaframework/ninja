package ninja.async;

import ninja.Context;
import ninja.Result;

/**
 * The strategy for async handling
 */
public interface AsyncStrategy {
    void handleAsync();
    Result controllerReturned();
    void returnResultAsync(Result result, Context context);
}
