/**
 * Copyright (C) 2012- the original author or authors.
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

import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import ninja.BaseAndClassicModules;
import ninja.postoffice.mock.PostofficeMockImpl;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThat;

public class PostofficeProviderTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void defaultImplementation() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);   
        
        ninjaProperties.setProperty(PostofficeConstant.postofficeImplementation, null);
        
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        
        PostofficeProvider postofficeProvider = injector.getInstance(PostofficeProvider.class);
        
        assertThat(postofficeProvider.get(), instanceOf(PostofficeMockImpl.class));
    }    
    
    @Test
    public void missingImplementationThrowsExceptionOnUseNotCreate() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);   
        
        ninjaProperties.setProperty(PostofficeConstant.postofficeImplementation, "not_existing_implementation");
        
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        
        Provider<Postoffice> provider = injector.getProvider(Postoffice.class);
        
        // this will not work => we expect a runtime exception...
        thrown.expect(RuntimeException.class);
        Postoffice postoffice = injector.getInstance(Postoffice.class);
    }
    
    @Test
    public void verifySingletonProviderAndInstance() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);   
        
        ninjaProperties.setProperty(PostofficeConstant.postofficeImplementation, null);
        
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));

        PostofficeProvider provider = injector.getInstance(PostofficeProvider.class);
        
        // cache provider should be a singleton
        assertThat(provider, sameInstance(injector.getInstance(PostofficeProvider.class)));
        assertThat(provider, sameInstance(injector.getInstance(PostofficeProvider.class)));
        
        Postoffice postoffice = provider.get();
        
        // cache should be a singleton
        assertThat(postoffice, sameInstance(provider.get()));
        assertThat(postoffice, sameInstance(injector.getInstance(Postoffice.class)));
    }
}
