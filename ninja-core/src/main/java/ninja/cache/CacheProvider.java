/**
 * Copyright (C) 2012-2015 the original author or authors.
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

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * A provider that determines which implementation to load as Cache based on 
 * the value of key {@link CacheConstant#CACHE_IMPLEMENTATION} in
 * {@link NinjaProperties} (aka application.conf).
 * 
 * If this variable is set the instance for that class is 
 * instantiated and used as cache implementation.
 * 
 * If the variable is not set {@link CacheEhCacheImpl} is used by default.
 * 
 * 
 * @author ra
 *
 */
@Singleton
public class CacheProvider implements Provider<Cache> {

    private final NinjaProperties ninjaProperties;
    private final Injector injector;

    private final Cache cache;
    private final Logger logger;

    @Inject
    public CacheProvider(
                         Injector injector, 
                         NinjaProperties ninjaProperties, 
                         Logger logger) {
        
        this.ninjaProperties = ninjaProperties;
        this.injector = injector;
        this.logger = logger;
        
      
        Class<? extends Cache> cacheClass = null;

        String cacheImplClassName = ninjaProperties.get(NinjaConstant.CACHE_IMPLEMENTATION);
        
        if (cacheImplClassName != null) {
            try {

                Class<?> clazz = Class.forName(cacheImplClassName);
                cacheClass = clazz.asSubclass(Cache.class);

                logger.info("Using the {} as implementation for caching.",  cacheClass);

            } catch (ClassNotFoundException e) {
                
                throw new RuntimeException(
                        "Class defined in configuration " + NinjaConstant.CACHE_IMPLEMENTATION +
                        "not found (" + cacheClass + ")", e);
                
            } catch (ClassCastException e) {
                
                throw new RuntimeException(
                        "Class defined in configuration " 
                                + NinjaConstant.CACHE_IMPLEMENTATION +
                                "is not an instance of interface cache (" 
                                + cacheClass + ")", e);
            }
        }

        if (cacheClass == null) {
                // load default implementation
                cacheClass = CacheEhCacheImpl.class;
                logger.info("Using default eh cache implementation. ({}) ", cacheClass);
            
        }

        cache = injector.getInstance(cacheClass);
      
    }

    @Override
    public Cache get() {
        // only called once => reference cached by guice...
        return cache;
        
    }
}
