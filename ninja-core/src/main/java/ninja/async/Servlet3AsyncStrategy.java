package ninja.async;

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
    public void controllerReturned() {
        // Do nothing
    }

    @Override
    public void requestComplete() {
        request.getAsyncContext().complete();
    }
}
