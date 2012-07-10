package ninja.async;

import java.util.concurrent.CountDownLatch;

/**
 * @author James Roper
 */
public class BlockingAsyncStrategy implements AsyncStrategy {
    private final CountDownLatch requestCompleteLatch = new CountDownLatch(1);
    @Override
    public void handleAsync() {
    }

    @Override
    public void controllerReturned() {
        try {
            requestCompleteLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void requestComplete() {
        requestCompleteLatch.countDown();
    }
}
