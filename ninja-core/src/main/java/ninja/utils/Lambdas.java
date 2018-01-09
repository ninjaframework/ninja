/**
 * Copyright (C) 2012-2018 the original author or authors.
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

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lambdas {
    private static final Logger log = LoggerFactory.getLogger(Lambdas.class);
    
    /**
     * The kind of lambda.  Lambdas in Java can either be references to methods
     * or anonymously defined.
     * https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
     */
    public static enum Kind {
        // Reference to a static method (e.g. ContainingClass::staticMethodName)
        STATIC_METHOD_REFERENCE,
        // Reference to an instance method of a specific object (e.g. specificObject::instanceMethodName)
        SPECIFIC_INSTANCE_METHOD_REFERENCE,
        // Reference to an instance method of an arbitrary object of a particular type (e.g. ContainingClass::instanceMethodName)
        ANY_INSTANCE_METHOD_REFERENCE,
        // Anonymously defined (e.g. () -> return "a"; )
        ANONYMOUS_METHOD_REFERENCE
    }
    
    public static class LambdaInfo {
        private final Object lambda;
        private final Kind kind;
        private final SerializedLambda serializedLambda;
        private final Method functionalMethod;
        private final Method implementationMethod;

        public LambdaInfo(Object lambda, Kind kind, SerializedLambda serializedLambda, Method functionalMethod, Method implementationMethod) {
            this.lambda = lambda;
            this.kind = kind;
            this.serializedLambda = serializedLambda;
            this.functionalMethod = functionalMethod;
            this.implementationMethod = implementationMethod;
        }

        public Object getLambda() {
            return lambda;
        }
        
        public Kind getKind() {
            return kind;
        }

        public SerializedLambda getSerializedLambda() {
            return serializedLambda;
        }

        public Method getFunctionalMethod() {
            return functionalMethod;
        }
        
        public Method getImplementationMethod() {
            return implementationMethod;
        }
        
        public boolean areMethodParameterCountsEqual() {
            // in case of captured arguments sometimes these do not match
            return this.functionalMethod.getParameterCount() ==
                    this.implementationMethod.getParameterCount();
        }
        
        @Override
        public String toString() {
            return new StringBuilder()
                .append("kind=").append(kind)
                .append(" func=").append(functionalMethod)
                .append(" impl=").append(implementationMethod)
                .append(" serialized=").append(serializedLambda)
                .toString();
        }
    }
    
    public static LambdaInfo reflect(Object lambda) {
        Objects.requireNonNull(lambda);
        
        // note: this throws a runtime exception if the lambda isn't serializable
        // or if the object isn't actually a lambda (e.g. an anon class)
        SerializedLambda serializedLambda = getSerializedLambda(lambda);
        
        Method functionalMethod;
        try {
            functionalMethod = getMethod(lambda.getClass(),
                serializedLambda.getFunctionalInterfaceMethodName());
            
            // important: only way classes other than the creator can invoke it
            functionalMethod.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to getFunctionalMethod", e);
        }
        
        Method implementationMethod;
        try {
            implementationMethod = getImplementationMethod(serializedLambda);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to getImplementationMethod", e);
        }
        
        Kind kind;
        
        // https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/MethodHandleInfo.html
        int implMethodKind = serializedLambda.getImplMethodKind();
        
        if (implMethodKind == 6) {  // REF_invokeStatic
            if (serializedLambda.getImplMethodName().startsWith("lambda$")) {
                kind = Kind.ANONYMOUS_METHOD_REFERENCE;
            } else {
                kind = Kind.STATIC_METHOD_REFERENCE;
            }
        } else {
            if (serializedLambda.getCapturedArgCount() > 0) {
                kind = Kind.SPECIFIC_INSTANCE_METHOD_REFERENCE;
            } else {
                kind = Kind.ANY_INSTANCE_METHOD_REFERENCE;
            }
        }
        
        return new LambdaInfo(lambda, kind, serializedLambda, functionalMethod, implementationMethod);
    }
    
    /**
     * Tries to get a SerializedLambda from an Object by searching the class
     * hierarchy for a <code>writeReplace</code> method.  The lambda must
     * be serializable in order for this method to return a value.
     * @param lambda An object that is an instance of a functional interface. 
     * @return The SerializedLambda
     */
    public static SerializedLambda getSerializedLambda(Object lambda) {
        Objects.requireNonNull(lambda);
        
        if (!(lambda instanceof java.io.Serializable)) {
            throw new IllegalArgumentException("Functional object does not implement java.io.Serializable");
        }

        for (Class<?> clazz = lambda.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method replaceMethod = clazz.getDeclaredMethod("writeReplace");
                replaceMethod.setAccessible(true);
                Object serializedForm = replaceMethod.invoke(lambda);

                if (serializedForm instanceof SerializedLambda) {
                    return (SerializedLambda) serializedForm;
                }
            } catch (NoSuchMethodError e) {
                // fall through the loop and try the next class
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Functional object is not a lambda");
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException("Unable to cleanly serialize lambda", e);
            }
        }

        throw new RuntimeException("writeReplace method not found");
    }
    
    public static Method getFunctionalMethod(SerializedLambda serializedLambda) throws NoSuchMethodException, ClassNotFoundException {
        return getMethod(serializedLambda.getFunctionalInterfaceClass(),
            serializedLambda.getFunctionalInterfaceMethodName());
    }
    
    public static Method getImplementationMethod(SerializedLambda serializedLambda) throws NoSuchMethodException, ClassNotFoundException {
        return getMethod(serializedLambda.getImplClass(),
            serializedLambda.getImplMethodName());
    }
    
    public static Method getMethod(String className, String methodName) throws NoSuchMethodException, ClassNotFoundException {
        Class<?> clazz = Class.forName(className.replace('/', '.'));
        return getMethod(clazz, methodName);
    }
    
    public static Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException, ClassNotFoundException {
        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
            // no match then try interfaces in order
            for (Class<?> interfaceClass : clazz.getInterfaces()) {
                for (Method method : interfaceClass.getDeclaredMethods()) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        
        throw new NoSuchMethodException("Method " + methodName + " not found in " + clazz);
    }
    
}
