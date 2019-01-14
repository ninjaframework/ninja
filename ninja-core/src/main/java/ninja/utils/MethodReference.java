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
import java.util.Objects;

/**
 * Compound key of a Class and the name of a method in it. Primarily used for
 * reverse route lookups.
 */
public class MethodReference {
    
    private final Class declaringClass;
    private final String methodName;

    public MethodReference(Class declaringClass, String methodName) {
        this.declaringClass = declaringClass;
        this.methodName = methodName;
    }
    
    public MethodReference(Method method) {
        this(method.getDeclaringClass(), method.getName());
    }

    public Class getDeclaringClass() {
        return declaringClass;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.declaringClass);
        hash = 83 * hash + Objects.hashCode(this.methodName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MethodReference other = (MethodReference) obj;
        if (!Objects.equals(this.methodName, other.methodName)) {
            return false;
        }
        return Objects.equals(this.declaringClass, other.declaringClass);
    }
    
}
