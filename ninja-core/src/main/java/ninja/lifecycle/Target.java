/**
 * Copyright (C) 2012 the original author or authors.
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

import java.lang.reflect.Method;

/**
 * A lifecycle target, ie something to start/stop
 *
 * @author James Roper
 */
class Target implements Comparable<Target> {
    private final Method startMethod;
    private final Object target;
    private final int order;

    Target(Method startMethod, Object target, int order) {
        this.startMethod = startMethod;
        this.target = target;
        this.order = order;
    }

    public Method getStartMethod() {
        return startMethod;
    }

    public Object getTarget() {
        return target;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(Target o) {
        return order - o.getOrder();
    }
}
