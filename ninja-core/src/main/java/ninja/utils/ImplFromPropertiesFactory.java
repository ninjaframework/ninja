/**
 * Copyright (C) the original author or authors.
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

import com.google.inject.Injector;
import org.slf4j.Logger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An abstract provider that determines which implementation to load based on 
 * the value of key in {@link NinjaProperties} (aka application.conf).
 * 
 * If this variable is set then the implementation class is loaded for subclasses
 * to use in their own implementation of Provider.get().
 */
public class ImplFromPropertiesFactory<T> {

    private final Injector injector;
    private final NinjaProperties ninjaProperties;
    private final String propertyName;
    private final Class<? extends T> targetClass;
    private final String defaultClassName;
    private final Logger logger;
    private final AtomicReference<Class<? extends T>> implementationClassRef;
    
    /**
     * Tries to load implementation class defined as a property in NinjaProperties
     * or will fallback to loading the default implementation class or nothing
     * if its not set.
     * 
     * @param injector The guice injector
     * @param ninjaProperties The ninja properties
     * @param propertyName The name of the configuration property key
     * @param targetClass The target implementation class (e.g. ninja.Cache)
     * @param defaultClassName The default implementation class or null if
     *          a default doesn't exist.  A string is used here to not require
     *          an implementation class to be on the classpath.
     * @param deferResolveUntilGet If false then the implementation class will
     *          be resolved in the constructor. If true then the resolution will
     *          be deferred until getImplementation() or create() is called.
     * @param logger The logger to use for info and errors
     */
    public ImplFromPropertiesFactory(Injector injector,
                                     NinjaProperties ninjaProperties,
                                     String propertyName,
                                     Class<? extends T> targetClass,
                                     String defaultClassName,
                                     boolean deferResolveUntilGet,
                                     Logger logger) {
        this.injector = injector;
        this.ninjaProperties = ninjaProperties;
        this.propertyName = propertyName;
        this.targetClass = targetClass;
        this.defaultClassName = defaultClassName;
        this.logger = logger;
        this.implementationClassRef = new AtomicReference<>();
        
        if (!deferResolveUntilGet) {
            this.implementationClassRef.set(resolveImplementationClass());
        }
    }
    
    public T create() {
        return this.injector
            .getInstance(getImplementationClass());
    }
    
    public Class<? extends T> getImplementationClass() {
        Class<? extends T> implementationClass = this.implementationClassRef.get();
        
        if (implementationClass != null) {
            return implementationClass;
        }
        
        // first one wins in case multiple threads get here...
        this.implementationClassRef.compareAndSet(null, resolveImplementationClass());
                
        return this.implementationClassRef.get();
    }
    
    private Class<? extends T> resolveImplementationClass() {
        String implementationClassName = ninjaProperties.get(propertyName);

        Class<? extends T> implementationClass = null;
        
        if (implementationClassName != null) {
            // resolve or throw exception
            implementationClass
                = resolveClass(implementationClassName,
                               targetClass,
                               propertyName,
                               true);
            logger.info("Using {} as implementation for {}", implementationClassName, targetClass.getCanonicalName());
        } else {
            if (defaultClassName != null) {
                // resolve or throw exception
                implementationClass
                    = resolveClass(defaultClassName,
                                   targetClass,
                                   propertyName,
                                   false);
                logger.info("Using {} as default implementation for {}", defaultClassName, targetClass.getCanonicalName());
            }
        }
        
        return implementationClass;
    }
    
    private Class<? extends T> resolveClass(String className, Class<? extends T> targetClass, String propertyName, boolean viaConfiguration) {
        // more descriptive error message
        String message = (viaConfiguration
                ? "in configuration " + propertyName : "as default for configuration " + propertyName);
        
        try {
            Class<?> resolvedClass = Class.forName(className);

            return resolvedClass.asSubclass(targetClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "Class defined " + message + " not found (" + className + ")", e);
        } catch (ClassCastException e) {
            throw new RuntimeException(
                "Class defined " + message + " is not an instance of ("
                + targetClass + ")", e);
        }
    }
}
