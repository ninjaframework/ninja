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

package ninja.servlet;

import ninja.Bootstrap;
import ninja.Context;
import ninja.Route;
import ninja.Router;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @author ra
 */
@RunWith(MockitoJUnitRunner.class)
public class NinjaServletBootstrapTest {
    
    NinjaPropertiesImpl ninjaPropertiesImpl;
    
    @Test
    public void userSuppliedServletModuleInConfDirectory() {

        ninjaPropertiesImpl = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build();
        
        Bootstrap bootstrap = new NinjaServletBootstrap(ninjaPropertiesImpl, Optional.empty());
        
        bootstrap.boot();
        
        assertThat(bootstrap.getInjector().getInstance(Context.class),
                is(instanceOf(NinjaServletContext.class)));
        
        assertThat(
                "Ninja Boostrap process picks up user supplied Guice servlet module in conf.ServletModule",
                bootstrap.getInjector().getInstance(conf.ServletModule.DummyInterfaceForTesting.class),
                is(instanceOf(conf.ServletModule.DummyClassForTesting.class)));
        
        
        Router router = bootstrap.getInjector().getInstance(Router.class);
        Route route = router.getRouteFor("GET", "/");

        assertThat("conf.Routes initialized properly. We get back the class we defined by the route.",
                route.getControllerClass(), is(instanceOf(controller.DummyControllerForTesting.class.getClass())));
    }
    
    @Test
    public void userSuppliedServletModuleInShiftedConfDirectory() {
        
        ninjaPropertiesImpl = Mockito.spy(ninjaPropertiesImpl = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build());
        
        Mockito.when(
                ninjaPropertiesImpl.get(NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE))
                .thenReturn("custom_base_package");
        
        Bootstrap bootstrap = new NinjaServletBootstrap(ninjaPropertiesImpl, Optional.empty());
        
        bootstrap.boot();
        
        assertThat(
                "Ninja Boostrap process picks up user supplied Guice servlet module in custom_base_package.conf.ServletModule",
                bootstrap.getInjector().getInstance(custom_base_package.conf.ServletModule.DummyInterfaceForTesting.class),
                is(instanceOf(custom_base_package.conf.ServletModule.DummyClassForTesting.class)));
        
        
        Router router = bootstrap.getInjector().getInstance(Router.class);
        Route route = router.getRouteFor("GET", "/custom_base_package");

        assertThat("custom_base_package.conf.Routes initialized properly. We get back the class we defined by the route.",
                route.getControllerClass(), is(instanceOf(controller.DummyControllerForTesting.class.getClass())));
    }
}
