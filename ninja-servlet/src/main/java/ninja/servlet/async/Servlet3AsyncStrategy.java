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

import javax.servlet.http.HttpServletRequest;

import ninja.Context;
import ninja.Result;
import ninja.utils.ResultHandler;

/**
 * @author James Roper
 */
public class Servlet3AsyncStrategy implements AsyncStrategy {
    private final ResultHandler resultHandler;
    private final HttpServletRequest request;

    public Servlet3AsyncStrategy(ResultHandler resultHandler,
                                 HttpServletRequest request) {
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
