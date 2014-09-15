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

package ninja.cache;

import ninja.Configuration;
import ninja.lifecycle.LifecycleSupport;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class CacheProviderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testThatSettingWrongCacheImplementationYieldsException() {

        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);

        ninjaProperties.setProperty(NinjaConstant.CACHE_IMPLEMENTATION, "not_existing_implementation");

        Injector injector = Guice.createInjector(new Configuration(ninjaProperties), LifecycleSupport.getModule());

        Logger logger = injector.getInstance(Logger.class);

        // this will not work => we expect a runtime exception...
        thrown.expect(RuntimeException.class);
        CacheProvider cacheProvider = new CacheProvider(
                injector,
                ninjaProperties,
                logger);

        cacheProvider.get();

    }

}
