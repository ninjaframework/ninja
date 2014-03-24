/**
 * Copyright (C) 2012-2014 the original author or authors.
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

package ninja.params;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ninja.Context;
import ninja.RoutingException;
import ninja.validation.Validator;
import ninja.validation.WithValidator;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

/**
 * Invokes methods on the controller, extracting arguments out
 *
 * @author James Roper
 */
public class ControllerMethodInvoker {
    private final Method method;
    private final ArgumentExtractor<?>[] argumentExtractors;

    ControllerMethodInvoker(Method method, ArgumentExtractor<?>[] argumentExtractors) {
        this.method = method;
        this.argumentExtractors = argumentExtractors;
    }

    public Object invoke(Object controller, Context context) {
        // Extract arguments
        Object[] arguments = new Object[argumentExtractors.length];
        for (int i = 0; i < argumentExtractors.length; i++) {
            arguments[i] = argumentExtractors[i].extract(context);
        }
        try {
            return method.invoke(controller, arguments);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

}
