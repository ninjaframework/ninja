package ninja.template;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import ninja.ContentTypes;
import ninja.Context;
import ninja.Result;
import ninja.utils.LoggerProvider;

import org.junit.Test;
import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class TemplateEngineManagerImplTest {

    @Test
    public void testGetJson() {
        assertThat(createTemplateEngineManager().getTemplateEngineForContentType(
                ContentTypes.APPLICATION_JSON), instanceOf(TemplateEngineJsonGson.class));
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

    public static abstract class MockTemplateEngine implements TemplateEngine {
        public void invoke(Context context, Result result) {

        }

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

    private TemplateEngineManager createTemplateEngineManager(final Class<?>... toBind) {
        return createInjector(toBind).getInstance(TemplateEngineManager.class);
    }

    private Injector createInjector(final Class<?>... toBind) {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            	
            	bind(Logger.class).toProvider(LoggerProvider.class);            	
            	
                for (Class<?> clazz : toBind) {
                	
                    bind(clazz);                 
                    
                }
            }
        });
    }
}
