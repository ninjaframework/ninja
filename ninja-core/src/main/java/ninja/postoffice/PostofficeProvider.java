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

package ninja.postoffice;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import ninja.utils.ImplFromPropertiesFactory;

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

import ninja.postoffice.mock.PostofficeMockImpl;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.slf4j.LoggerFactory;

/**
 * A provider that determines which implementation to load as Postoffice based on 
 * the value of a configuration key in {@link NinjaProperties} (aka application.conf).
 * 
 * If this variable is set the instance for that class is 
 * instantiated and used as postoffice implementation.
 * 
 * If the variable is not set {@link PostofficeMockImpl} is used by default in
 * dev/test mode and PostofficeCommonsmailImpl in prod.
 */
@Singleton
public class PostofficeProvider implements Provider<Postoffice> {
    static private final Logger logger = LoggerFactory.getLogger(PostofficeProvider.class);
    
    private final ImplFromPropertiesFactory<Postoffice> factory;
    private final Supplier<Postoffice> supplier;
    
    @Inject
    public PostofficeProvider(Injector injector, NinjaProperties ninjaProperties) {
        this.factory = new ImplFromPropertiesFactory<>(
            injector,
            ninjaProperties,
            PostofficeConstant.postofficeImplementation,
            Postoffice.class,
            (ninjaProperties.isProd() ?
                  "ninja.postoffice.commonsmail.PostofficeCommonsmailImpl"
                  : PostofficeMockImpl.class.getCanonicalName()),
            true,
            logger);
        
        // lazy singleton
        this.supplier = Suppliers.memoize(new Supplier<Postoffice>() {
            @Override
            public Postoffice get() {
                return factory.create();
            }
        });
    }

    @Override
    public Postoffice get() {
        return this.supplier.get();
    }
   
}