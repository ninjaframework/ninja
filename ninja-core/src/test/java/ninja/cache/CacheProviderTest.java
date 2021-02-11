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

package ninja.cache;

import ninja.BaseAndClassicModules;
import ninja.lifecycle.LifecycleSupport;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.inject.Provider;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThat;

public class CacheProviderTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void defaultImplementation() {
        NinjaPropertiesImpl ninjaProperties = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build();
        
        ninjaProperties.setProperty(NinjaConstant.CACHE_IMPLEMENTATION, null);
        
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        
        CacheProvider cacheProvider = injector.getInstance(CacheProvider.class);
        
        assertThat(cacheProvider.get(), instanceOf(CacheEhCacheImpl.class));
    }
    
    @Test
    public void configuredImplementation() {
        NinjaPropertiesImpl ninjaProperties = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build();  
        
        ninjaProperties.setProperty(NinjaConstant.CACHE_IMPLEMENTATION, CacheMemcachedImpl.class.getName());
        // just a dummy to test that loading works
        ninjaProperties.setProperty(NinjaConstant.MEMCACHED_HOST, "127.0.0.1:1234");
        
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        
        Provider<Cache> cacheProvider = injector.getProvider(Cache.class);
        
        assertThat(cacheProvider.get(), instanceOf(CacheMemcachedImpl.class));
    }
    
    @Test
    public void missingImplementationThrowsExceptionOnUseNotCreate() {
        NinjaPropertiesImpl ninjaProperties = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build();   
        
        ninjaProperties.setProperty(NinjaConstant.CACHE_IMPLEMENTATION, "not_existing_implementation");
        
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        
        Provider<Cache> provider = injector.getProvider(Cache.class);
        
        // this will not work => we expect a runtime exception...
        thrown.expect(RuntimeException.class);
        Cache cache = injector.getInstance(Cache.class);
    }
    
    @Test
    public void verifySingletonProviderAndInstance() {
        NinjaPropertiesImpl ninjaProperties = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build(); 
        
        ninjaProperties.setProperty(NinjaConstant.CACHE_IMPLEMENTATION, CacheMockImpl.class.getCanonicalName());
        
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));

        CacheProvider cacheProvider = injector.getInstance(CacheProvider.class);
        
        // cache provider should be a singleton
        assertThat(cacheProvider, sameInstance(injector.getInstance(CacheProvider.class)));
        assertThat(cacheProvider, sameInstance(injector.getInstance(CacheProvider.class)));
        
        Cache cache = cacheProvider.get();
        
        // cache should be a singleton
        assertThat(cache, sameInstance(cacheProvider.get()));
        assertThat(cache, sameInstance(injector.getInstance(Cache.class)));
    }

}
