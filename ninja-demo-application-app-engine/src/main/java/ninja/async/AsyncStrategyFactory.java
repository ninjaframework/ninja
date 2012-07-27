package ninja.async;

import ninja.utils.ResultHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Factory for creating an async strategy
 *
 * @author James Roper
 */
public interface AsyncStrategyFactory {
    AsyncStrategy createStrategy(HttpServletRequest request, ResultHandler resultHandler);
}
