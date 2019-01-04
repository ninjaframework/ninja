/**
 * Copyright (C) 2012-2019 the original author or authors.
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

package ninja.utils;

import java.lang.reflect.Method;
import java.util.Optional;
import ninja.ControllerMethods.ControllerMethod;

public class LambdaRoute {
    
    private final Method functionalMethod;
    private final Optional<Method> implementationMethod;
    private final Optional<Object> targetObject;

    public LambdaRoute(Method functionalMethod, Method implementationMethod, Object targetObject) {
        this.functionalMethod = functionalMethod;
        this.implementationMethod = Optional.ofNullable(implementationMethod);
        this.targetObject = Optional.ofNullable(targetObject);
    }

    public Method getFunctionalMethod() {
        return functionalMethod;
    }

    public Optional<Method> getImplementationMethod() {
        return implementationMethod;
    }

    public Optional<Object> getTargetObject() {
        return targetObject;
    }
    
    static public LambdaRoute resolve(ControllerMethod controllerMethod) {
        try {
            Lambdas.LambdaInfo lambdaInfo = Lambdas.reflect(controllerMethod);

            switch (lambdaInfo.getKind()) {
                case ANY_INSTANCE_METHOD_REFERENCE:
                case STATIC_METHOD_REFERENCE:
                    // call impl method just like before Java 8
                    return new LambdaRoute(lambdaInfo.getImplementationMethod(), null, null);
                case SPECIFIC_INSTANCE_METHOD_REFERENCE:
                case ANONYMOUS_METHOD_REFERENCE:
                    // only safe to use the impl method for argument types if
                    // the number of arguments matches between the methods
                    if (lambdaInfo.areMethodParameterCountsEqual()) {
                        return new LambdaRoute(
                            lambdaInfo.getFunctionalMethod(),
                            lambdaInfo.getImplementationMethod(),
                            controllerMethod);
                    }
            }
        } catch (IllegalArgumentException e) {
            // unable to detect lambda (e.g. such as anonymous/concrete class)
        }
        
        // fallback to simple call the "apply" method on the supplied method instance
        try {
            Method functionalMethod = Lambdas.getMethod(controllerMethod.getClass(), "apply");
            functionalMethod.setAccessible(true);
            return new LambdaRoute(functionalMethod, null, controllerMethod);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
}
