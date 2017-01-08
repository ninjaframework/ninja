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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.internal.ProviderMethod;
import com.google.inject.spi.DefaultBindingScopingVisitor;
import com.google.inject.spi.ProviderInstanceBinding;

/**
 * Implementation of the lifecycle service
 */
public class LifecycleServiceImpl implements LifecycleService {
    private static final Logger log = LoggerFactory.getLogger(LifecycleServiceImpl.class);
    private final Injector injector;
    private final LifecycleRegister lifecycleRegister;
    private final LifecycleSupport lifecycleSupport;
    private volatile State state = State.STOPPED;
    private volatile long startTime;

    @Inject
    public LifecycleServiceImpl(Injector injector, LifecycleRegister lifecycleRegister, LifecycleSupport lifecycleSupport) {
        this.lifecycleRegister = lifecycleRegister;
        this.injector = injector;
        this.lifecycleSupport = lifecycleSupport;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        log.info("Starting Ninja application...");
        state = State.STARTING;
        // First, ensure that all singleton scoped bindings are instantiated, so that they can be started.  It is not
        // until they are instantiated that LifecycleSupport has an opportunity to register them.
        for (final Binding binding : injector.getBindings().values()) {
            binding.acceptScopingVisitor(new DefaultBindingScopingVisitor() {
                @Override
                public Object visitEagerSingleton() {
                    injector.getInstance(binding.getKey());
                    return null;
                }

                @Override
                public Object visitScope(Scope scope) {
                    if (scope.equals(Scopes.SINGLETON)) {
                        Object target = injector.getInstance(binding.getKey());
                        if (binding instanceof ProviderInstanceBinding) {
                            Provider providerInstance = ((ProviderInstanceBinding) binding).getProviderInstance();
                            if (providerInstance instanceof ProviderMethod) {
                                // @Provides methods don't get picked up by TypeListeners, so we need to manually register them
                                if (lifecycleSupport.hasLifecycleMethod(target.getClass())) {
                                    lifecycleSupport.registerLifecycle(target);
                                }
                            }
                        }
                    }
                    return null;
                }

            });
        }
        lifecycleRegister.start();
        long time = System.currentTimeMillis() - startTime;
        log.info("Ninja application started in {}ms", time);
        state = lifecycleRegister.isStarted() ? State.STARTED : State.STOPPED;
    }

    @Override
    public void stop() {
        long start = System.currentTimeMillis();
        log.info("Stopping Ninja application...");
        state = State.STOPPING;
        lifecycleRegister.stop();
        long time = System.currentTimeMillis() - start;
        log.info("Ninja application stopped in {}ms", time);
        state = lifecycleRegister.isStarted() ? State.STARTED : State.STOPPED;
    }

    @Override
    public boolean isStarted() {
        return lifecycleRegister.isStarted();
    }

    @Override
    public State getState() {
        return state;
    }

    public long getUpTime() {
        if (isStarted()) {
            return System.currentTimeMillis() - startTime;
        } else {
            return 0;
        }
    }
}
