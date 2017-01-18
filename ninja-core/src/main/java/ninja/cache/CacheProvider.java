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

package ninja.cache;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import ninja.utils.ImplFromPropertiesFactory;
import org.slf4j.LoggerFactory;

/**
 * A provider that determines which implementation to load as a Cache based on 
 * the value of a configuration key in {@link NinjaProperties} (aka application.conf).
 * 
 * The configured implementation is only resolved when its actually used vs.
 * at application startup.
 * 
 * If this variable is set the instance for that class is 
 * instantiated and used as cache implementation.
 * 
 * If the variable is not set {@link CacheEhCacheImpl} is used by default.
 */
@Singleton
public class CacheProvider implements Provider<Cache> {
    static private final Logger logger = LoggerFactory.getLogger(CacheProvider.class);
    
    private final ImplFromPropertiesFactory<Cache> factory;
    private final Supplier<Cache> supplier;
    
    @Inject
    public CacheProvider(Injector injector, NinjaProperties ninjaProperties) {
        this.factory = new ImplFromPropertiesFactory<>(
            injector,
            ninjaProperties,
            NinjaConstant.CACHE_IMPLEMENTATION,
            Cache.class,
            "ninja.cache.CacheEhCacheImpl",
            true,
            logger);
        
        // lazy singleton
        this.supplier = Suppliers.memoize(new Supplier<Cache>() {
            @Override
            public Cache get() {
                return factory.create();
            }
        });
    }

    @Override
    public Cache get() {
        return this.supplier.get();
    }
    
}
