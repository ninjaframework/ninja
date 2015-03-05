/**
 * Copyright (C) 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja.template;

import com.google.common.base.Optional;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.inject.Singleton;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Route;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.session.FlashScope;
import ninja.session.Session;
import static ninja.template.TemplateEngineFreemarker.FREEMARKER_CONFIGURATION_FILE_SUFFIX;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerTest {

    @Mock
    Lang lang;

    @Mock
    Logger logger;

    @Mock
    TemplateEngineHelper templateEngineHelper;

    @Mock
    TemplateEngineManager templateEngineManager;

    @Mock
    TemplateEngineFreemarkerReverseRouteMethod templateEngineFreemarkerReverseRouteMethod;

    @Mock
    TemplateEngineFreemarkerAssetsAtMethod templateEngineFreemarkerAssetsAtMethod;

    @Mock
    TemplateEngineFreemarkerWebJarsAtMethod templateEngineFreemarkerWebJarsAtMethod;

    @Mock
    NinjaProperties ninjaProperties;

    @Mock
    Messages messages;

    @Mock
    Context context;

    @Mock
    Result result;
    
    @Mock
    Route route;

    TemplateEngineFreemarker templateEngineFreemarker;

    Writer writer;

    @Before
    public void before() throws Exception {
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

        
        when(lang.getLanguage(any(Context.class), any(Optional.class))).thenReturn(Optional.<String>absent());

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
    public void testBasicInvocation() throws Exception {
        templateEngineFreemarker.invoke(context, Results.ok());
        verify(ninjaProperties).getWithDefault(TemplateEngineFreemarker.FREEMARKER_CONFIGURATION_FILE_SUFFIX, ".ftl.html");
        assertThat(templateEngineFreemarker.getSuffixOfTemplatingEngine(), equalTo(".ftl.html"));
        verify(templateEngineHelper).getTemplateForResult(eq(route), any(Result.class), eq(".ftl.html"));
        assertThat(writer.toString(), equalTo("Just a plain template for testing..."));
    }
    
    @Test
    public void testThatConfigurationCanBeRetrieved() throws Exception {
        templateEngineFreemarker.invoke(context, Results.ok());
        assertThat(templateEngineFreemarker.getConfiguration(), CoreMatchers.notNullValue(Configuration.class));
    }

    @Test
    public void testThatWhenNotProdModeDoesNotThrowTemplateException() {
        when(templateEngineHelper.getTemplateForResult(any(Route.class), any(Result.class), Mockito.anyString())).thenReturn("views/broken.ftl.html");
        // only freemarker templates generated exceptions to browser -- it makes
        // sense that this continues in diagnostic mode only
        when(ninjaProperties.isDev()).thenReturn(true);
        when(ninjaProperties.isDiagnostic()).thenReturn(true);
        templateEngineFreemarker.invoke(context, Results.ok());
        // this string will be contained in response
        assertThat(writer.toString(), CoreMatchers.containsString("Ninja Diagnostic Error"));
    }

    @Test(expected = RuntimeException.class)
    public void testThatProdModeThrowsTemplateException() throws RuntimeException {
        when(templateEngineHelper.getTemplateForResult(any(Route.class), any(Result.class), Mockito.anyString())).thenReturn("views/broken.ftl.html");
        when(ninjaProperties.isProd()).thenReturn(true);
        templateEngineFreemarker.invoke(context, Results.ok());
    }
}
