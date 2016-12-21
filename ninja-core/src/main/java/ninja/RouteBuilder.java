/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import ninja.utils.MethodReference;

public interface RouteBuilder extends WithControllerMethod<Void> {

    RouteBuilder route(String uri);

    void with(Class<?> controllerClass, String controllerMethod);

    @Deprecated
    void with(MethodReference controllerMethodRef);
    
    /**
     * A static result to return for this route.
     * @param result The result to return on every request.
     * @deprecated Use the functional interface methods to supply a new result
     *      for each route request.  Its recommended to use <code>() -> Results.redirect("/")</code>.
     */
    @Deprecated
    void with(Result result);

}