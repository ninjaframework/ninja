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

package ninja.servlet.async;

import javax.servlet.http.HttpServletRequest;

import ninja.utils.ResultHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author James Roper
 */
public class AsyncStrategyFactoryHolder {
    private static final Logger log = LoggerFactory
            .getLogger(AsyncStrategyFactoryHolder.class);
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
            // The code above might throw an AbstractMethodError or a
            // NoSuchMethodError,
            // if it does, it means async is not supported
            return false;
        }
    }
}
