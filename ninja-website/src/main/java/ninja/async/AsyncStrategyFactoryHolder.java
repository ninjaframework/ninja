package ninja.async;

import ninja.utils.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @author James Roper
 */
public class AsyncStrategyFactoryHolder {
    private static final Logger log = LoggerFactory.getLogger(AsyncStrategyFactoryHolder.class);
    public static final AsyncStrategyFactory INSTANCE;

    static {
        // Try to detect servlet 3
        AsyncStrategyFactory factory;
        try {
            HttpServletRequest.class.getMethod("startAsync");
            factory = new AsyncStrategyFactory() {
                @Override
                public AsyncStrategy createStrategy(HttpServletRequest request,
                        ResultHandler resultHandler) {
                    return new Servlet3AsyncStrategy(resultHandler, request);
                }
            };
        } catch (NoSuchMethodException e) {
            log.warn("Servlet 3 container not detected, async controllers will block");
            factory = new AsyncStrategyFactory() {
                @Override
                public AsyncStrategy createStrategy(HttpServletRequest request,
                        ResultHandler resultHandler) {
                    return new BlockingAsyncStrategy();
                }
            };
        }
        INSTANCE = factory;
    }
}
