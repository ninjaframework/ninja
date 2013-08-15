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

import java.util.Locale;

import ninja.i18n.Messages;

import org.rythmengine.RythmEngine;
import org.rythmengine.extension.II18nMessageResolver;
import org.rythmengine.template.ITemplate;

import com.google.common.base.Optional;

/**
 * I18n message resolver for Rythm template
 * @author sojin
 * 
 */
public class TemplateEngineRythmI18nMessageResolver implements II18nMessageResolver {

    final Messages messages;

    public TemplateEngineRythmI18nMessageResolver(Messages messages) {
        this.messages = messages;
    }

    @Override
    public String getMessage(ITemplate template, String key, Object... args) {

        Locale locale = null;
        if (args.length > 0) {
            Object arg0 = args[0];
            if (arg0 instanceof Locale) {
                locale = (Locale) arg0;
                Object[] args0 = new Object[args.length - 1];
                System.arraycopy(args, 1, args0, 0, args.length - 1);
                args = args0;
            }
        }
        
        if (locale == null && template != null) {
            locale = (template == null) ? RythmEngine.get().renderSettings.locale() : template.__curLocale();
        }

        Optional<String> lang = Optional.absent();
        if (locale != null) {
            // to conform to rfc5646 and BCP 47
            lang = Optional.of(locale.toString().replace('_', '-'));
        }

        Optional<String> i18nMessage = Optional.absent();
        i18nMessage = messages.get(key, lang, args);

        return i18nMessage.or("");
    }
}
