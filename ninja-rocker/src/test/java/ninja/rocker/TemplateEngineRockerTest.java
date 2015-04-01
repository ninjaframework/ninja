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
package ninja.rocker;

import static ninja.template.TemplateEngineFreemarker.FREEMARKER_CONFIGURATION_FILE_SUFFIX;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.google.common.base.Optional;
import com.google.inject.Provider;

import freemarker.template.Configuration;
import java.io.ByteArrayOutputStream;
import ninja.AssetsController;
import ninja.Router;
import ninja.template.TemplateEngineHelper;
import ninja.template.TemplateEngineManager;
import org.junit.Assert;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineRockerTest {

    @Mock
    Lang lang;

    @Mock
    Logger logger;

    @Mock
    TemplateEngineHelper templateEngineHelper;

    @Mock
    TemplateEngineManager templateEngineManager;

    @Mock
    NinjaProperties ninjaProperties;

    @Mock
    Messages messages;

    @Mock
    Context context;
    
    @Mock
    Router router;
    
    @Mock
    Route route;

    NinjaRockerContext ninjaRockerContext;
    
    TemplateEngineRocker templateEngineRocker;

    ByteArrayOutputStream baos;

    @Before
    public void before() throws Exception {
        //Setup that allows to to execute invoke(...) in a very minimal version.
        //when(ninjaProperties.getWithDefault(FREEMARKER_CONFIGURATION_FILE_SUFFIX, ".ftl.html")).thenReturn(".ftl.html");
       
        Provider<Lang> langProvider = new Provider<Lang>() {
            @Override
            public Lang get() {
                return lang;
            }
        };
        
        ninjaRockerContext
            = new NinjaRockerContextImpl(
                router,
                messages,
                langProvider,
                ninjaProperties,
                null);
        
        templateEngineRocker
            = new TemplateEngineRocker(
                ninjaRockerContext,
                templateEngineHelper,
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

        baos = new ByteArrayOutputStream();
        ResponseStreams responseStreams = mock(ResponseStreams.class);
        when(context.finalizeHeaders(any(Result.class))).thenReturn(responseStreams);
        when(responseStreams.getOutputStream()).thenReturn(baos);
    }

    @Test
    public void engineHasSingletonAnnotation() {
        Singleton singleton = TemplateEngineRocker.class.getAnnotation(Singleton.class);
        assertThat(singleton, notNullValue());
    }

    @Test(expected = RuntimeException.class)
    public void engineWithNoRenderableThrowsRuntimeException() throws RuntimeException {
        templateEngineRocker.invoke(context, Results.ok());
    }
    
    @Test
    public void simpleView() throws Exception {
        Result r = Results.ok().render(
            ninja.rocker.views.Simple.template()
        );
        
        templateEngineRocker.invoke(context, r);
        
        assertThat(baos.toString("UTF-8"), equalTo("Hello!"));
    }
    
    @Test
    public void isProdView() throws Exception {
        when(ninjaProperties.isProd()).thenReturn(true);
        
        Result r = Results.ok().render(
            ninja.rocker.views.IsProd.template()
        );
        
        templateEngineRocker.invoke(context, r);
        
        assertThat(baos.toString("UTF-8"), equalTo("PROD"));
    }
    
    @Test
    public void reverseRouteView() throws Exception {
        when(router.getReverseRoute(TemplateEngineRockerTest.class, "index")).thenReturn("/this/is/a/test/reverse/route");
        
        Result r = Results.ok().render(
            ninja.rocker.views.ReverseRoute.template()
        );
        
        templateEngineRocker.invoke(context, r);
        
        assertThat(baos.toString("UTF-8"), equalTo("/this/is/a/test/reverse/route"));
    }
    
    @Test
    public void assetsAtView() throws Exception {
        // assetsAt uses the reverseRoute for AssetsController to do the lookup...
        when(router.getReverseRoute(AssetsController.class, "serveStatic", "fileName", "test.png")).thenReturn("/assets/test/test.png");
        
        Result r = Results.ok().render(
            ninja.rocker.views.AssetsAt.template()
        );
        
        templateEngineRocker.invoke(context, r);
        
        assertThat(baos.toString("UTF-8"), equalTo("/assets/test/test.png"));
    }
    
    @Test
    public void contextPathView() throws Exception {
        when(context.getContextPath()).thenReturn("/mycontext");
        
        Result r = Results.ok().render(
            ninja.rocker.views.ContextPath.template()
        );
        
        templateEngineRocker.invoke(context, r);
        
        assertThat(baos.toString("UTF-8"), equalTo("/mycontext"));
    }
    
}
