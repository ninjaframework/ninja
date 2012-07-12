package ninja.async;

import ninja.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * @author James Roper
 */
public class Servlet3AsyncStrategy implements AsyncStrategy {
    private final HttpServletRequest request;

    public Servlet3AsyncStrategy(HttpServletRequest request) {
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
    public void returnResultAsync(Result result) {

        request.getAsyncContext().complete();
    }
}
