/**
 * Copyright (C) 2012-2020 the original author or authors.
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

package ninja.metrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for marking a controller method to be Counted for metrics
 * collection.
 *
 * A counter increments on method execution and optionally decrements at execution completion.
 *
 * If no name is specified, the controller classname and method name are used.
 *
 * @author James Moger
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Counted {

    String value() default "";

    /**
     * Determines the behavior of the counter.
     * <p/>
     * if false (default), the counter will continuously increment and will
     * indicate the number of times this method has been executed.
     * <p/>
     * if true, the counter will be incremented before the method is executed
     * and will be decremented when method execution has completed - regardless
     * of thrown exceptions.
     * <p/>
     * This is useful for determining the realtime execution status of a method.
     */
    boolean active() default false;

}
