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

package ninja.template;

import freemarker.template.Configuration;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Route;
import ninja.exceptions.RenderingException;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import javax.inject.Singleton;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static ninja.template.TemplateEngineFreemarker.FREEMARKER_CONFIGURATION_FILE_SUFFIX;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerTest {

    @Mock
    private Lang lang;

    @Mock
    private Logger logger;

    @Mock
    private TemplateEngineHelper templateEngineHelper;

    @Mock
    private TemplateEngineManager templateEngineManager;

    @Mock
    private TemplateEngineFreemarkerReverseRouteMethod templateEngineFreemarkerReverseRouteMethod;

    @Mock
    private TemplateEngineFreemarkerAssetsAtMethod templateEngineFreemarkerAssetsAtMethod;

    @Mock
    private TemplateEngineFreemarkerWebJarsAtMethod templateEngineFreemarkerWebJarsAtMethod;

    @Mock
    private NinjaProperties ninjaProperties;

    @Mock
    private Messages messages;

    @Mock
    private Context context;

    @Mock
    private Result result;
    
    @Mock
    private Route route;

    private TemplateEngineFreemarker templateEngineFreemarker;

    private Writer writer;

    @Before
    public final void before() throws Exception {
        //Setup that allows to to execute invoke(...) in a very minimal version.
        when(ninjaProperties.getWithDefault(FREEMARKER_CONFIGURATION_FILE_SUFFIX, ".ftl.html")).thenReturn(".ftl.html");
       
        templateEngineFreemarker
                = new TemplateEngineFreemarker(
                        messages,
                        lang,
                        logger,
                        templateEngineHelper,
                        templateEngineManager,
                        templateEngineFreemarkerReverseRouteMethod,
                        templateEngineFreemarkerAssetsAtMethod,
                        templateEngineFreemarkerWebJarsAtMethod,
                        ninjaProperties);

        
        when(lang.getLanguage(any(Context.class), any(Optional.class))).thenReturn(Optional.<String>empty());

        Session session = Mockito.mock(Session.class);
        when(session.isEmpty()).thenReturn(true);
        when(context.getSession()).thenReturn(session);
        when(context.getRoute()).thenReturn(route);
        when(lang.getLocaleFromStringOrDefault(any(Optional.class))).thenReturn(Locale.ENGLISH);

        FlashScope flashScope = Mockito.mock(FlashScope.class);
        Map<String, String> flashScopeData = new HashMap<>();
        when(flashScope.getCurrentFlashCookieData()).thenReturn(flashScopeData);
        when(context.getFlashScope()).thenReturn(flashScope);

        when(templateEngineHelper.getTemplateForResult(any(Route.class), any(Result.class), Mockito.anyString())).thenReturn("views/template.ftl.html");

        writer = new StringWriter();
        ResponseStreams responseStreams = mock(ResponseStreams.class);
        when(context.finalizeHeaders(any(Result.class))).thenReturn(responseStreams);
        when(responseStreams.getWriter()).thenReturn(writer);
        
        
    }

    @Test
    public void testThatTemplateEngineFreemarkerHasSingletonAnnotation() {
        Singleton singleton = TemplateEngineFreemarker.class.getAnnotation(Singleton.class);
        assertThat(singleton, notNullValue());
    }

    @Test
    public void testBasicInvocation() {
        templateEngineFreemarker.invoke(context, Results.ok());
        verify(ninjaProperties).getWithDefault(TemplateEngineFreemarker.FREEMARKER_CONFIGURATION_FILE_SUFFIX, ".ftl.html");
        assertThat(templateEngineFreemarker.getSuffixOfTemplatingEngine(), equalTo(".ftl.html"));
        verify(templateEngineHelper).getTemplateForResult(eq(route), any(Result.class), eq(".ftl.html"));
        assertThat(writer.toString(), equalTo("Just a plain template for testing..."));
    }
    
    @Test
    public void testThatConfigurationCanBeRetrieved() {
        templateEngineFreemarker.invoke(context, Results.ok());
        assertThat(templateEngineFreemarker.getConfiguration(), CoreMatchers.notNullValue(Configuration.class));
    }

    @Test
    public void testThatWhenNotProdModeThrowsRenderingException() {
        when(templateEngineHelper.getTemplateForResult(any(Route.class), any(Result.class), Mockito.anyString())).thenReturn("views/broken.ftl.html");
        // only freemarker templates generated exceptions to browser -- it makes
        // sense that this continues in diagnostic mode only
        //when(ninjaProperties.isDev()).thenReturn(true);
        //when(ninjaProperties.areDiagnosticsEnabled()).thenReturn(true);
        
        try {
            templateEngineFreemarker.invoke(context, Results.ok());
            fail("exception expected");
        } catch (RenderingException e) {
            // expected
        }
    }

    @Test(expected = RuntimeException.class)
    public void testThatProdModeThrowsTemplateException() throws RuntimeException {
        when(templateEngineHelper.getTemplateForResult(any(Route.class), any(Result.class), Mockito.anyString())).thenReturn("views/broken.ftl.html");
        templateEngineFreemarker.invoke(context, Results.ok());
    }
}
