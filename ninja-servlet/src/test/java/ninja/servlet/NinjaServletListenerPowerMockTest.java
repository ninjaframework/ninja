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

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import ninja.websockets.jsr356.Jsr356WebSockets;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.websocket.server.ServerContainer;

import static org.junit.Assert.assertFalse;

// TODO: PowerMock does not work on Java17+
@Ignore
@PrepareForTest(Class.class)
@RunWith(PowerMockRunner.class)
// PowerMockIgnore needed to deal with https://github.com/powermock/powermock/issues/864
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*"})
public class NinjaServletListenerPowerMockTest {

    @Mock
    private ServletContextEvent servletContextEvent;

    @Mock
    private ServletContext servletContext;

    @Before
    public final void before() {
        Mockito.when(servletContextEvent.getServletContext()).thenReturn(servletContext);
        Mockito.when(servletContext.getContextPath()).thenReturn("/contextpath");
    }

    @Test
    public void makeSureContextInitializedWorksWhenJettyThrowsException() throws Exception {
        // GIVEN 
        // we simulate an environment that does not have any websocket classes
        // on the classpath.
        PowerMockito.mockStatic(Class.class);
        PowerMockito
                .when(Class.forName(Mockito.eq("javax.websocket.server.ServerContainer"), Mockito.eq(false), Mockito.any()))
                .thenThrow(new ClassNotFoundException("Exception triggered by test"));
        
        // WHEN
        NinjaServletListener ninjaServletListener = new NinjaServletListener();
        ninjaServletListener.contextInitialized(servletContextEvent);
       
        // THEN
        // we expect that no websocket class has been configured in guice
        Injector injector = ninjaServletListener.getInjector();
        
        assertFalse(classAvailableFromInjector(ServerContainer.class, injector));
        assertFalse(classAvailableFromInjector(Jsr356WebSockets.class, injector));
    }
    
    private boolean classAvailableFromInjector(Class<?> clazz, Injector injector) { 
        try {
            injector.getInstance(clazz);
            return true;
        } catch (ConfigurationException c) {
            return false;
        }
    }

}
