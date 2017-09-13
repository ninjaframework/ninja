/**
 * Copyright (C) 2012-2017 the original author or authors.
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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.Map;

import ninja.Context;
import ninja.session.Session;
import ninja.template.directives.TemplateEngineFreemarkerAuthenticityFormDirective;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * @author svenkubiak
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerAuthenticityFormDirectiveTest {
       @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    Context context;

    @Mock
    Session session;

    StringWriter stringWriter = new StringWriter();

    Environment environment = new Environment(Mockito.mock(Template.class), null, stringWriter);

    TemplateEngineFreemarkerAuthenticityFormDirective templateEngineFreemarkerAuthenticityFormDirective;

    Map<String, String> parameters = Maps.newHashMap();

    @Before
    public void before() {
        when(context.getSession()).thenReturn(session);
        when(session.getAuthenticityToken()).thenReturn("12345");

        templateEngineFreemarkerAuthenticityFormDirective = new TemplateEngineFreemarkerAuthenticityFormDirective(context);
    }

    @Test
    public void testThatAuthenticityTokenIsNotCalledInConstructor() throws Exception {
        Mockito.verify(session, Mockito.never()).getAuthenticityToken();
    }
    
    @Test
    public void testThatItWorks() throws Exception {
        TemplateModel[] loopVars = new TemplateModel[0];

        templateEngineFreemarkerAuthenticityFormDirective.execute(environment, parameters, loopVars, null);

        assertThat(
                stringWriter.toString(),
                equalTo("<input type=\"hidden\" value=\"12345\" name=\"authenticityToken\" />"));
        Mockito.verify(session).getAuthenticityToken();
    }
    


    @Test
    public void testThatParamsThrowException() throws Exception {
        thrown.expect(TemplateException.class);
        parameters.put("foo", "bar");

        templateEngineFreemarkerAuthenticityFormDirective.execute(null, parameters, null, null);
    }

    @Test
    public void testThatLoopVarsThrowException() throws Exception {
        TemplateModel[] loopVars = new TemplateModel[1];
        thrown.expect(TemplateException.class);
        loopVars[0] = new TemplateModel() {};

        templateEngineFreemarkerAuthenticityFormDirective.execute(null, parameters, loopVars, null);
    }
}