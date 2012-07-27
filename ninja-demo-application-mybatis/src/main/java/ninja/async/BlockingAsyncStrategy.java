package ninja.async;

import ninja.Context;
import ninja.Result;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author James Roper
 */
public class BlockingAsyncStrategy implements AsyncStrategy {
    private final CountDownLatch requestCompleteLatch = new CountDownLatch(1);
    private final AtomicReference<Result> result = new AtomicReference<Result>();
    @Override
    public void handleAsync() {
    }

    @Override
    public Result controllerReturned() {
        try {
            requestCompleteLatch.await();
            return this.result.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnResultAsync(Result result, Context context) {
        this.result.set(result);
        requestCompleteLatch.countDown();
    }
}
