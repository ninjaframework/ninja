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

package ninja.servlet;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author ra
 */
@RunWith(MockitoJUnitRunner.class)
public class NinjaServletListenerTest {
    
    @Mock
    ServletContextEvent servletContextEvent;
    
    @Mock
    ServletContext servletContext;
    
    String CONTEXT_PATH = "/contextpath";
    
    @Before
    public void before() {
    
        Mockito.when(servletContextEvent.getServletContext()).thenReturn(servletContext);
        Mockito.when(servletContext.getContextPath()).thenReturn(CONTEXT_PATH);
        
    }

    @Test
    public void testCreatingInjectorWithoutContextAndOrPropertiesWorks() {
        
        NinjaServletListener ninjaServletListener = new NinjaServletListener();
        ninjaServletListener.contextInitialized(servletContextEvent);
        
        Injector injector = ninjaServletListener.getInjector();
        
        NinjaProperties ninjaProperties = injector.getInstance(NinjaProperties.class);
        
        // make sure we are using the context path from the serveltcontext here
        assertThat(ninjaProperties.getContextPath(), equalTo(CONTEXT_PATH));
        
    }
    
    @Test
    public void testCreatingInjectorWithCustomNinjaPropertiesWorks() {
        
        // setup stuff
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);
        ninjaProperties.setProperty("key!", "value!");
        
        NinjaServletListener ninjaServletListener = new NinjaServletListener();
        ninjaServletListener.setNinjaProperties(ninjaProperties);
                
        // start the injector:
        ninjaServletListener.contextInitialized(servletContextEvent);
        
        // test stuff
        Injector injector = ninjaServletListener.getInjector();
        NinjaProperties ninjaPropertiesFromServer = injector.getInstance(NinjaProperties.class);

        
        assertThat(ninjaPropertiesFromServer.get("key!"), equalTo("value!"));
        // make sure we are using the context path from the serveltcontext here
        assertThat(ninjaProperties.getContextPath(), equalTo(CONTEXT_PATH));
        
    }
    
    
    @Test
    public void testThatContextDestroyedWorks() {
        
        NinjaServletListener ninjaServletListener = new NinjaServletListener();
        ninjaServletListener.contextInitialized(servletContextEvent);
        
        // Before we destroy stuff the injector is there
        assertThat(ninjaServletListener.getInjector(), notNullValue());
        
        ninjaServletListener.contextDestroyed(servletContextEvent);
        
        // After destroying the context the injector is null.
        assertThat(ninjaServletListener.getInjector(), nullValue());
        
    }
    
    @Test
    public void testCallingGetInjectorMultipleTimesWorks() {
        
        NinjaServletListener ninjaServletListener = new NinjaServletListener();
        ninjaServletListener.contextInitialized(servletContextEvent);
        
        Injector injector = ninjaServletListener.getInjector();
                
        for (int i = 0; i < 100; i++) {
            // make sure that we are getting back the very same injector all the
            // time
            assertThat(ninjaServletListener.getInjector(), equalTo(injector));
            
        }
        
        
    }
    
    @Test
    public void testThatSettingNinjaPropertiesTwiceDoesNotWork() {
        
        NinjaServletListener ninjaServletListener = new NinjaServletListener();
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);
        
        // first setting works
        ninjaServletListener.setNinjaProperties(ninjaProperties);
        
        
        boolean gotException = false;
        
        try {
            //setting the properties a second time does not work...
            ninjaServletListener.setNinjaProperties(ninjaProperties);
            
        } catch (IllegalStateException illegalStateException) {
            
            gotException = true;
        }
        
        assertThat(gotException, equalTo(true));
        
    }
    
    
    
}
