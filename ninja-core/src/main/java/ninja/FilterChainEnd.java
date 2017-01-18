/**
 * Copyright (C) 2012-2017 the original author or authors.
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
    
    private final Provider<?> targetObjectProvider;
    private final ControllerMethodInvoker controllerMethodInvoker;

    FilterChainEnd(Provider<?> targetObjectProvider,
                   ControllerMethodInvoker controllerMethodInvoker) {
        this.targetObjectProvider = targetObjectProvider;
        this.controllerMethodInvoker = controllerMethodInvoker;
    }

    @Override
    public Result next(Context context) {
        Result controllerResult = (Result)controllerMethodInvoker.invoke(
            targetObjectProvider.get(), context);

        if (controllerResult instanceof AsyncResult) {
            // Make sure handle async has been called
            context.handleAsync();
            Result newResult = context.controllerReturned();
            if (newResult != null) {
                controllerResult = newResult;
            }
        }

        return controllerResult;
    }
}