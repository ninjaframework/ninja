/**
 * Copyright (C) 2013 the original author or authors.
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

import static org.mockito.Mockito.when;

import java.util.Locale;

import ninja.i18n.Messages;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.rythmengine.template.ITemplate;

import com.google.common.base.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineRythmI18nMessageResolverTest {

    @Mock
    Messages messages;

    @Mock
    ITemplate template;

    @Test
    public void testGetMessage() {
        TemplateEngineRythmI18nMessageResolver resolver = new TemplateEngineRythmI18nMessageResolver(
                messages);

        Optional<String> lang = Optional.absent();
        when(messages.get(Mockito.eq("key"), Mockito.eq(lang), Mockito.eq("arg1"))).thenReturn(
                Optional.of("i18n-Message"));
        Assert.assertEquals("i18n-Message",
                resolver.getMessage(template, "key", "arg1"));

        lang = Optional.of("de");
        when(messages.get(Mockito.eq("key"), Mockito.eq(lang), Mockito.eq("arg1"))).thenReturn(
                Optional.of("i18n-Nachricht"));
        Assert.assertEquals("i18n-Nachricht",
                resolver.getMessage(template, "key", new Locale("de"), "arg1"));
    }
}
