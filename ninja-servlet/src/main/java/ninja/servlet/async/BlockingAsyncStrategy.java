/**
 * Copyright (C) 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.servlet.async;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import ninja.Context;
import ninja.Result;

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
