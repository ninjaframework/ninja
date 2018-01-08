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

package ninja.template;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import ninja.ContentTypes;
import ninja.Context;
import ninja.Result;
import ninja.Router;
import ninja.RouterImpl;
import ninja.i18n.Lang;
import ninja.i18n.LangImpl;
import ninja.utils.LoggerProvider;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;

import org.junit.Test;
import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class TemplateEngineManagerImplTest {

    @Test
    public void testGetJson() {
        assertThat(createTemplateEngineManager().getTemplateEngineForContentType(
                ContentTypes.APPLICATION_JSON), instanceOf(TemplateEngineJson.class));
    }

    @Test
    public void testGetJsonP() {
        assertThat(createTemplateEngineManager().getTemplateEngineForContentType(
                ContentTypes.APPLICATION_JSONP), instanceOf(TemplateEngineJsonP.class));
    }

    @Test
    public void testGetFreemarker() {
        assertThat(createTemplateEngineManager().getTemplateEngineForContentType(
                ContentTypes.TEXT_HTML), instanceOf(TemplateEngineFreemarker.class));
    }

    @Test
    public void testGetCustom() {
        assertThat(createTemplateEngineManager(CustomTemplateEngine.class).getTemplateEngineForContentType(
                "custom"), instanceOf(CustomTemplateEngine.class));
    }

    @Test
    public void testOverrideJson() {
        assertThat(createTemplateEngineManager(OverrideJsonTemplateEngine.class).getTemplateEngineForContentType(
                ContentTypes.APPLICATION_JSON), instanceOf(OverrideJsonTemplateEngine.class));
    }

    @Test
    public void testOverrideHtml() {
        assertThat(createTemplateEngineManager(OverrideHtmlTemplateEngine.class).getTemplateEngineForContentType(
                ContentTypes.TEXT_HTML), instanceOf(OverrideHtmlTemplateEngine.class));
    }
    
    @Test
    public void testOverrideHtmlOrderMatters() {
        TemplateEngineManager templateEngineManager
            = createTemplateEngineManager(
                OverrideHtmlTemplateEngine.class,
                OverrideHtmlTemplateEngine3.class,
                OverrideHtmlTemplateEngine2.class);
        
        assertThat(templateEngineManager.getTemplateEngineForContentType(
                ContentTypes.TEXT_HTML), instanceOf(OverrideHtmlTemplateEngine2.class));
    }

    @Test
    public void testContentTypes() {
        List<String> types = Lists.newArrayList(createTemplateEngineManager().getContentTypes());
        Collections.sort(types);
        assertThat(types.toString(),
                equalTo("[application/javascript, application/json, application/xml, text/html, text/plain]"));
    }

	@Test
	public void testGetNonExistingProducesNoNPE() {
		TemplateEngineManager manager = createTemplateEngineManager(OverrideJsonTemplateEngine.class);
		assertNull(manager.getTemplateEngineForContentType("non/existing"));
	}

    public static abstract class MockTemplateEngine implements TemplateEngine {
        @Override
        public void invoke(Context context, Result result) {

        }

        @Override
        public String getSuffixOfTemplatingEngine() {
            return null;

        }
    }

    public static class CustomTemplateEngine extends MockTemplateEngine {
        @Override
        public String getContentType() {
            return "custom";
        }
    }

    public static class OverrideJsonTemplateEngine extends MockTemplateEngine {
        @Override
        public String getContentType() {
            return ContentTypes.APPLICATION_JSON;
        }
    }

    public static class OverrideHtmlTemplateEngine extends MockTemplateEngine {
        @Override
        public String getContentType() {
            return ContentTypes.TEXT_HTML;
        }
    }
    
    public static class OverrideHtmlTemplateEngine2 extends MockTemplateEngine {
        @Override
        public String getContentType() {
            return ContentTypes.TEXT_HTML;
        }
    }
    
    public static class OverrideHtmlTemplateEngine3 extends MockTemplateEngine {
        @Override
        public String getContentType() {
            return ContentTypes.TEXT_HTML;
        }
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
                
                bind(TemplateEngineText.class);
                bind(TemplateEngineFreemarker.class);
                bind(TemplateEngineJson.class);
                bind(TemplateEngineJsonP.class);
                bind(TemplateEngineXml.class);

                bind(NinjaProperties.class).toInstance(new NinjaPropertiesImpl(NinjaMode.test));

                for (Class<?> clazz : toBind) {

                    bind(clazz);

                }
            }
        });
    }
}
