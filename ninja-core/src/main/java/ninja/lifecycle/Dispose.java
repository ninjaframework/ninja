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

package ninja.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating this method should be called on application shut down.
 * <p/>
 * Note that a reference is held to this bean by the lifecycle system, if this bean is not a singleton, and gets
 * instantiated in response to regular events, you will run out of memory.
 * <p/>
 * Note: If this bean is provided by an @Provided method, then that method *must* be annotated with @Singleton,
 * otherwise it won't be detected.
 *
 * @author James Roper
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dispose {
    /**
     * The order in which it should be disposed, higher meaning earlier.  While apps are free to use any ordering system
     * they wish, the following convention is recommended:
     * <p/>
     * 10 - Services that connect to resources and do not depend on other services, for example, database connections
     * 20-80 - Services that depend on resources, but may still be needed for the app to complete its core functions
     * 90 - Services that stop the app from doing its core functions, eg listening on queues, responding to HTTP requests
     *
     * @return The order, the greatest being stopped first, the least being stopped last.
     */
    int order() default 50;
}
