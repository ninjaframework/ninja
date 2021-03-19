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

package ninja;

import java.util.Optional;
import ninja.cache.Cache;
import static org.junit.Assert.assertTrue;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author ra
 */
@RunWith(MockitoJUnitRunner.class)
public class BootstrapTest {
    
    NinjaPropertiesImpl ninjaPropertiesImpl;

    @Test
    public void testInitializeWithAllUserSpecifiedThingsInConfDirectory() {

        ninjaPropertiesImpl = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build();
        
        Bootstrap bootstrap = new Bootstrap(ninjaPropertiesImpl, Optional.empty());
        
        bootstrap.boot();

        assertThat(
                "Ninja Boostrap process picks up user supplied conf.Ninja definition",
                bootstrap.getInjector().getInstance(ninja.Ninja.class),
                is(instanceOf(conf.Ninja.class)));

        assertThat(
                "Ninja Boostrap process picks up user supplied Guice module in conf.Module",
                bootstrap.getInjector().getInstance(conf.Module.DummyInterfaceForTesting.class),
                is(instanceOf(conf.Module.DummyClassForTesting.class)));
        
        
        Router router = bootstrap.getInjector().getInstance(Router.class);
        Route route = router.getRouteFor("GET", "/");

        assertThat(
                "conf.Routes initialized properly. We get back the class we defined by the route.",
                route.getControllerClass(), is(instanceOf(com.example.controllers.DummyApplication.class.getClass())));
    }
    
    @Test
    public void noUserSuppliedThingsInConfDirectory() {
        // since we needed to supply conf.Ninja, etc. for our other tests, we'll 
        // test a user NOT supplying these by configuring the application base package
        // a bit of a hack, but will work to force NOT finding anything
        
        ninjaPropertiesImpl = Mockito.spy(ninjaPropertiesImpl = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build());
        
        Mockito.when(
                ninjaPropertiesImpl.get(NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE))
                .thenReturn("com.doesnotexist");
        
        Bootstrap bootstrap = new Bootstrap(ninjaPropertiesImpl, Optional.empty());
        
        bootstrap.boot();

        Router router = bootstrap.getInjector().getInstance(Router.class);
        
        try {
            Route route = router.getRouteFor("GET", "/");
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("routes not compiled"));
        }
    }
    

    @Test
    public void testInitializeWithAllUserSpecifiedThingsInShiftedConfDirectory() {
        
        ninjaPropertiesImpl = Mockito.spy(ninjaPropertiesImpl = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build());
        
        Mockito.when(
                ninjaPropertiesImpl.get(NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE))
                .thenReturn("com.example");
        
        Bootstrap bootstrap = new Bootstrap(ninjaPropertiesImpl, Optional.empty());
        
        bootstrap.boot();
        
        assertThat(
                "Ninja Boostrap process picks up user supplied conf.Ninja definition",
                bootstrap.getInjector().getInstance(ninja.Ninja.class),
                is(instanceOf(com.example.conf.Ninja.class)));

        assertThat(
                "Ninja Boostrap process picks up user supplied Guice module in conf.Module",
                bootstrap.getInjector().getInstance(com.example.conf.Module.DummyInterfaceForTesting.class),
                is(instanceOf(com.example.conf.Module.DummyClassForTesting.class)));
        
        
        Router router = bootstrap.getInjector().getInstance(Router.class);
        Route route = router.getRouteFor("GET", "/");

        assertThat(
                "conf.Routes initialized properly. We get back the class we defined by the route.",
                route.getControllerClass(), is(instanceOf(com.example.controllers.DummyApplication.class.getClass())));
    }
    
    @Test
    public void frameworkModuleSkipsNinjaClassicModule() {
        ninjaPropertiesImpl = Mockito.spy(ninjaPropertiesImpl = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build());
        
        Mockito.when(
                ninjaPropertiesImpl.get(NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE))
                .thenReturn("com.example.frameworkmodule");
        
        Bootstrap bootstrap = new Bootstrap(ninjaPropertiesImpl, Optional.empty());
        
        bootstrap.boot();

        try {
            Cache cache = bootstrap.getInjector().getInstance(Cache.class);
            fail("cache should not have been found");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("No implementation for Cache was bound"));
        }
    }
}
