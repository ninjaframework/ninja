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
    private static volatile AsyncStrategyFactory instance;

    public static AsyncStrategyFactory getInstance(HttpServletRequest request) {
        if (instance == null) {
            AsyncStrategyFactory factory;
            if (isAsyncSupported(request)) {
                factory = new AsyncStrategyFactory() {
                    @Override
                    public AsyncStrategy createStrategy(HttpServletRequest request,
                            ResultHandler resultHandler) {
                        return new Servlet3AsyncStrategy(resultHandler, request);
                    }
                };
            } else {
                log.warn("Servlet 3 container not detected, async controllers will block");
                factory = new AsyncStrategyFactory() {
                    @Override
                    public AsyncStrategy createStrategy(HttpServletRequest request,
                            ResultHandler resultHandler) {
                        return new BlockingAsyncStrategy();
                    }
                };
            }
            instance = factory;
        }
        return instance;
    }

    private static boolean isAsyncSupported(HttpServletRequest request) {
        try {
            return request.isAsyncSupported();
        } catch (LinkageError error) {
            // The code above might throw an AbstractMethodError or a NoSuchMethodError,
            // if it does, it means async is not supported
            return false;
        }
    }
}
