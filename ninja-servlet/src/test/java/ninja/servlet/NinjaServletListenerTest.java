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

package ninja.servlet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import ninja.Router;
import ninja.servlet.conf.Module;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

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
    public void testCreatingInjectorWithCustomModulesPackageWorks() {

        // setup stuff
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);
        ninjaProperties.setProperty(NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE, "ninja.servlet");

        NinjaServletListener ninjaServletListener = new NinjaServletListener();
        ninjaServletListener.setNinjaProperties(ninjaProperties);

        // start the injector:
        ninjaServletListener.contextInitialized(servletContextEvent);

        // test stuff
        Injector injector = ninjaServletListener.getInjector();
        Router router = injector.getInstance((Router.class));

        //router is initialized otherwise there will be exception that routes isn't compiled
        router.getRouteFor("GET", "/");

        //validate that main application module is initialized
        Boolean mainModuleConstant = injector.getInstance(Key.get(Boolean.class, Names.named(Module.TEST_CONSTANT_NAME)));
        assertThat(mainModuleConstant, is(true));

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
