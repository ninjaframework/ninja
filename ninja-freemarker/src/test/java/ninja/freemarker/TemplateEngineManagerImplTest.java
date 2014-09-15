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

package ninja.freemarker;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import ninja.ContentTypes;
import ninja.Router;
import ninja.RouterImpl;
import ninja.i18n.Lang;
import ninja.i18n.LangImpl;
import ninja.template.TemplateEngineManager;
import ninja.utils.LoggerProvider;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;

import org.junit.Test;
import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class TemplateEngineManagerImplTest {

    @Test
    public void testGetFreemarker() {
        assertThat(createTemplateEngineManager(TemplateEngineFreemarker.class).getTemplateEngineForContentType(
                ContentTypes.TEXT_HTML), instanceOf(TemplateEngineFreemarker.class));
    }

    private TemplateEngineManager createTemplateEngineManager(final Class<?>... toBind) {
        return createInjector(toBind).getInstance(TemplateEngineManager.class);
    }

    private Injector createInjector(final Class<?>... toBind) {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

            	bind(Logger.class).toProvider(LoggerProvider.class);
            	bind(Lang.class).to(LangImpl.class);
                bind(Router.class).to(RouterImpl.class);

            	bind(NinjaProperties.class).toInstance(new NinjaPropertiesImpl(NinjaMode.test));

                for (Class<?> clazz : toBind) {

                    bind(clazz);

                }
            }
        });
    }
}
