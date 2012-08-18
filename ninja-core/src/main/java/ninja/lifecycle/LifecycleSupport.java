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

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Method;

/**
 * Guice support for lifecycle
 *
 * @author James Roper
 */
public class LifecycleSupport {
    private final LifecycleRegister lifecycleRegister = new LifecycleRegister();

    /**
     * Get the lifecycle support module
     *
     * @return The lifecycle support module
     */
    public static Module getModule() {
        return new LifecycleSupport().constructModule();
    }

    private LifecycleSupport() {
    }

    private class LifecycleAnnotatedListener implements TypeListener {
        @Override
        public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            if (hasLifecycleMethod(type.getRawType())) {
                // Add the listener
                encounter.register(new LifecycleListener<I>());
            }
        }
    }

    private class LifecycleListener<I> implements InjectionListener<I> {
        @Override
        public void afterInjection(final I injectee) {
            registerLifecycle(injectee);
        }
    }

    public boolean hasLifecycleMethod(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (method.getAnnotation(Start.class) != null || method.getAnnotation(Dispose.class) != null) {
                return true;
            }
        }
        return false;
    }

    public void registerLifecycle(Object target) {
        for (final Method method : target.getClass().getMethods()) {
            Start start = method.getAnnotation(Start.class);
            if (start != null) {
                lifecycleRegister.registerStartable(new Target(method, target, start.order()));
            }
            Dispose dispose = method.getAnnotation(Dispose.class);
            if (dispose != null) {
                lifecycleRegister.registerDisposable(new Target(method, target, dispose.order()));
            }
        }
    }

    private Module constructModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bindListener(Matchers.any(), new LifecycleAnnotatedListener());
                // Also, bind the lifecycle register
                bind(LifecycleRegister.class).toInstance(lifecycleRegister);
                bind(LifecycleSupport.class).toInstance(LifecycleSupport.this);
            }
        };
    }


}
