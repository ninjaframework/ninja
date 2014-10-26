/*
 * Copyright 2014 ra.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.servlet;

import static org.junit.Assert.assertTrue;
import ninja.Route;
import ninja.Router;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author ra
 */
@RunWith(MockitoJUnitRunner.class)
public class NinjaBootstrapTest {
    

    NinjaPropertiesImpl ninjaPropertiesImpl;
    

    @Test
    public void testInitializeWithAllUserSpecifiedThingsInConfDirectory() {
        
        // Using the real class is simpler because it loads a 
        // lot of stuff from application.conf.
        ninjaPropertiesImpl = new NinjaPropertiesImpl(NinjaMode.test);
        
        NinjaBootstrap ninjaBootstrap = new NinjaBootstrap(ninjaPropertiesImpl);
        
        ninjaBootstrap.boot();
        
        assertTrue(
                "Ninja Boostrap process picks up user supplied conf.Ninja definition",
                ninjaBootstrap.getInjector().getInstance(ninja.Ninja.class) 
                instanceof conf.Ninja);
        
       
        assertTrue(
                "Ninja Boostrap process picks up user supplied Guice module in conf.Module",
                ninjaBootstrap.getInjector().getInstance(conf.Module.DummyInterfaceForTesting.class) 
                instanceof conf.Module.DummyClassForTesting);
        

        assertTrue(
                "Ninja Boostrap process picks up user supplied Guice servlet module in conf.ServletModule",
                ninjaBootstrap.getInjector().getInstance(conf.ServletModule.DummyInterfaceForTesting.class) 
                instanceof conf.ServletModule.DummyClassForTesting);
        
        
        Router router = ninjaBootstrap.getInjector().getInstance(Router.class);
        Route route = router.getRouteFor("GET", "/");

        assertTrue("conf.Routes initialized properly. We get back the class we defined by the route.",
                route.getControllerClass() == controller.DummyControllerForTesting.class);
                
    
    }
    
    @Test
    public void testInitializeWithAllUserSpecifiedThingsInShiftedConfDirectory() {
        
        ninjaPropertiesImpl = Mockito.spy(new NinjaPropertiesImpl(NinjaMode.test));
        
        Mockito.when(
                ninjaPropertiesImpl.get(NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE))
                .thenReturn("custom_base_package");
        
        NinjaBootstrap ninjaBootstrap = new NinjaBootstrap(ninjaPropertiesImpl);
        
        ninjaBootstrap.boot();
        
        assertTrue(
                "Ninja Boostrap process picks up user supplied custom_base_package.conf.Ninja definition",
                ninjaBootstrap.getInjector().getInstance(ninja.Ninja.class) 
                instanceof custom_base_package.conf.Ninja);
        
       
        assertTrue(
                "Ninja Boostrap process picks up user supplied Guice module in custom_base_package.conf.Module",
                ninjaBootstrap.getInjector().getInstance(custom_base_package.conf.Module.DummyInterfaceForTesting.class) 
                instanceof custom_base_package.conf.Module.DummyClassForTesting);
        

        assertTrue(
                "Ninja Boostrap process picks up user supplied Guice servlet module in custom_base_package.conf.ServletModule",
                ninjaBootstrap.getInjector().getInstance(custom_base_package.conf.ServletModule.DummyInterfaceForTesting.class) 
                instanceof custom_base_package.conf.ServletModule.DummyClassForTesting);
        
        
        Router router = ninjaBootstrap.getInjector().getInstance(Router.class);
        Route route = router.getRouteFor("GET", "/custom_base_package");

        assertTrue("custom_base_package.conf.Routes initialized properly. We get back the class we defined by the route.",
                route.getControllerClass() == controller.DummyControllerForTesting.class);
                
    
    }
}
