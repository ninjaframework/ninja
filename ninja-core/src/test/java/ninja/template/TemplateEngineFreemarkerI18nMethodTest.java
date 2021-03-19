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

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ninja.Context;
import ninja.Result;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.validation.ConstraintViolation;
import ninja.validation.IsInteger;
import ninja.validation.Validators.JSRValidator;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.Appender;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 *
 * @author ra
 */
@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerI18nMethodTest {

    @Mock
    private Context context;
    
    @Mock
    private Result result;
    
    @Mock
    private Messages messages;
    
    @Rule 
    public final ExpectedException thrown= ExpectedException.none();
    
    @Captor
    private ArgumentCaptor<List<String>> listCaptor;

    private TemplateEngineFreemarkerI18nMethod templateEngineFreemarkerI18nMethod;

    private Appender mockAppender;
    
    @Before
    public final void before() {
        templateEngineFreemarkerI18nMethod
                = Mockito.spy(new TemplateEngineFreemarkerI18nMethod(
                        messages, 
                        context, 
                        result));
        
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        mockAppender = Mockito.mock(Appender.class);
        root.addAppender(mockAppender);
    }
    
    
    @Test
    public void testThatNoKeyYieldsException() throws Exception {
        
        List args = Collections.EMPTY_LIST;
        
        thrown.expect(TemplateModelException.class);
        
        templateEngineFreemarkerI18nMethod.exec(args);
        
    }
    
    @Test
    public void testThatSingleKeyWithValueWorks() throws Exception {

        Optional<Result> resultOptional = Optional.of(result);
        
        Mockito.when(
                messages.get("my.message.key", context, resultOptional))
                .thenReturn(Optional.of("This simulates the translated message!"));
        
        List args = new ArrayList();
        args.add(new SimpleScalar("my.message.key"));
        
        
        TemplateModel returnValue 
                = templateEngineFreemarkerI18nMethod.exec(args);
        
        assertThat(((SimpleScalar) returnValue).getAsString(), CoreMatchers.equalTo("This simulates the translated message!"));
        
        Mockito.verify(mockAppender, Mockito.never()).doAppend(Matchers.anyObject());

    }
    
    @Test
    public void testThatSingleKeyWithMissingValueReturnsDefaultKey() throws Exception {

        Optional<Result> resultOptional = Optional.of(result);
        
        Mockito.when(
                messages.get("my.message.key", context, resultOptional))
                .thenReturn(Optional.<String>empty());
        
        List args = new ArrayList();
        args.add(new SimpleScalar("my.message.key"));
        
        
        TemplateModel returnValue 
                = templateEngineFreemarkerI18nMethod.exec(args);
        
        assertThat(((SimpleScalar) returnValue).getAsString(), CoreMatchers.equalTo("my.message.key"));
        
        // There must have been logged something because we did not find
        // the value for the key...
        Mockito.verify(mockAppender).doAppend(Matchers.anyObject());
    }
    
    @Test
    public void testThatKeyWithPlaceholderWorks() throws Exception {
        
        Optional<Result> resultOptional = Optional.of(result);
        
        List args = new ArrayList();
        args.add(new SimpleScalar("my.message.key"));
        args.add(new SimpleScalar("1000"));
        
        Mockito.when(
                messages.get(
                        Matchers.eq("my.message.key"), 
                        Matchers.eq(context), 
                        Matchers.eq(resultOptional),
                        Matchers.any(Object.class)))
                .thenReturn(Optional.of("This simulates the translated message number 1000!"));
        
        
        TemplateModel returnValue 
                = templateEngineFreemarkerI18nMethod.exec(args);
        
        assertThat(((SimpleScalar) returnValue).getAsString(), CoreMatchers.equalTo("This simulates the translated message number 1000!"));
        
        Mockito.verify(mockAppender, Mockito.never()).doAppend(Matchers.anyObject());
    }
    
    @Test
    public void testThatKeyWithPlaceholderReturnsDefaultKeyWhenKeyCannotBeFound() throws Exception {
        
        Optional<Result> resultOptional = Optional.of(result);
        
        List args = new ArrayList();
        args.add(new SimpleScalar("my.message.key"));
        args.add(new SimpleScalar("1000"));
        
        Mockito.when(
                messages.get(
                        Matchers.eq("my.message.key"), 
                        Matchers.eq(context), 
                        Matchers.eq(resultOptional),
                        Matchers.any(Object.class)))
                .thenReturn(Optional.<String>empty());
        
        TemplateModel returnValue 
                = templateEngineFreemarkerI18nMethod.exec(args);
        
        assertThat(((SimpleScalar) returnValue).getAsString(), CoreMatchers.equalTo("my.message.key"));
        
        // There must have been logged something because we did not find
        // the value for the key...
        Mockito.verify(mockAppender).doAppend(Matchers.anyObject());
        
    }
    
    @Test
    public void testThatConstraintViolationWorks() throws Exception {

        Optional<Result> resultOptional = Optional.of(result);
        
        Mockito.when(
                messages.get(IsInteger.KEY, context, resultOptional))
                .thenReturn(Optional.of("This simulates the translated message!"));
        
        ConstraintViolation violation = new ConstraintViolation(IsInteger.KEY, "theField", IsInteger.MESSAGE);
        
        List args = new ArrayList();
        args.add(new StringModel(violation, new BeansWrapper()));
        
        
        TemplateModel returnValue = templateEngineFreemarkerI18nMethod.exec(args);
        
        assertThat(((SimpleScalar) returnValue).getAsString(), CoreMatchers.equalTo("This simulates the translated message!"));
        
        Mockito.verify(mockAppender, Mockito.never()).doAppend(Matchers.anyObject());

    }
    
    @Test
    public void testThatConstraintViolationWorksWithDefault() throws Exception {

        Optional<Result> resultOptional = Optional.of(result);
        
        Mockito.when(
                messages.get(IsInteger.KEY, context, resultOptional))
                .thenReturn(Optional.empty());
        
        ConstraintViolation violation = new ConstraintViolation(IsInteger.KEY, "theField", IsInteger.MESSAGE);
        
        List args = new ArrayList();
        args.add(new StringModel(violation, new BeansWrapper()));
        
        
        TemplateModel returnValue = templateEngineFreemarkerI18nMethod.exec(args);
        
        assertThat(((SimpleScalar) returnValue).getAsString(), CoreMatchers.equalTo("theField must be an integer"));
        
        Mockito.verify(mockAppender, Mockito.never()).doAppend(Matchers.anyObject());

    }
}
