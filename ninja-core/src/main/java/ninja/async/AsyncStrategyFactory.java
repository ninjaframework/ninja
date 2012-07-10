package ninja.async;

import javax.servlet.http.HttpServletRequest;

/**
 * Factory for creating an async strategy
 *
 * @author James Roper
 */
public interface AsyncStrategyFactory {
    AsyncStrategy createStrategy(HttpServletRequest request);
}
