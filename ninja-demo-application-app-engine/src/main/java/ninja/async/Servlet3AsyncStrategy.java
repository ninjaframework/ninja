package ninja.async;

import ninja.Context;
import ninja.Result;
import ninja.utils.ResultHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author James Roper
 */
public class Servlet3AsyncStrategy implements AsyncStrategy {
    private final ResultHandler resultHandler;
    private final HttpServletRequest request;

    public Servlet3AsyncStrategy(ResultHandler resultHandler, HttpServletRequest request) {
        this.resultHandler = resultHandler;
        this.request = request;
    }

    @Override
    public void handleAsync() {
        request.startAsync();
    }

    @Override
    public Result controllerReturned() {
        return null;
    }

    @Override
    public void returnResultAsync(Result result, Context context) {
        resultHandler.handleResult(result, context);
        request.getAsyncContext().complete();
    }
}
