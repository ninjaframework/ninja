/**
 * Copyright (C) 2013 the original author or authors.
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

package ninja;

import ninja.params.ControllerMethodInvoker;
import com.google.inject.Provider;

/**
 * The end of the filter chain
 * 
 * @author James Roper
 */
class FilterChainEnd implements FilterChain {
    private final Provider<?> controllerProvider;
    private final ControllerMethodInvoker controllerMethodInvoker;

    FilterChainEnd(Provider<?> controllerProvider,
                   ControllerMethodInvoker controllerMethodInvoker) {
        this.controllerProvider = controllerProvider;
        this.controllerMethodInvoker = controllerMethodInvoker;
    }

    @Override
    public Result next(Context context) {

        Result result;

        result = (Result) controllerMethodInvoker.invoke(
                controllerProvider.get(), context);

        if (result instanceof AsyncResult) {
            // Make sure handle async has been called
            context.handleAsync();
            Result newResult = context.controllerReturned();
            if (newResult != null) {
                result = newResult;
            }
        }

        return result;
    }
}
