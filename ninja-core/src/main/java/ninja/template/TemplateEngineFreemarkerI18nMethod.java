/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import java.util.List;

import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class TemplateEngineFreemarkerI18nMethod implements
        TemplateMethodModelEx {
    
    public final static Logger logger 
            = LoggerFactory.getLogger(TemplateEngineFreemarkerAssetsAtMethod.class);

    final Messages messages;
    final Context context;
    final Optional<Result> result;

    public TemplateEngineFreemarkerI18nMethod(Messages messages,
                                              Context context,
                                              Result result) {
        this.messages = messages;
        this.context = context;
        this.result = Optional.of(result);

    }

    public TemplateModel exec(List args) throws TemplateModelException {

        if (args.size() == 1) {
            
            String messageKey = ((SimpleScalar) args.get(0)).getAsString();

            String messageValue = messages
                    .get(messageKey, context, result)
                    .or(messageKey);
            
            logIfMessageKeyIsMissing(messageKey, messageValue);
            
            return new SimpleScalar(messageValue);
            

        } else if (args.size() > 1) {

            List<String> strings = Lists.newArrayList();
            
            for (Object o : args) {
                
                // We currently allow only numbers and strings as arguments
                if (o instanceof SimpleScalar) {
                    strings.add(((SimpleScalar) o).getAsString());
                } else if  (o instanceof SimpleNumber) {
                    strings.add(((SimpleNumber) o).toString());
                }
                
            }
            
            String messageKey = strings.get(0);

            String messageValue 
                    = messages.get(
                            messageKey,
                            context, 
                            result, 
                            strings.subList(1, strings.size()).toArray())
                    .or(messageKey);
            
            logIfMessageKeyIsMissing(messageKey, messageValue);
            
            return new SimpleScalar(messageValue);

        } else {
            throw new TemplateModelException(
                "Using i18n without any key is not possible.");
        }

    }
    
    public void logIfMessageKeyIsMissing(
            String messageKey,
            String messageValue) {
        
        // If key equals value then Messages gave us back the key as value
        // We have to tell the user...
        if (messageKey.equals(messageValue)) {
            logger.error(
                "Message key {} missing. Using key as value inside template"
                        + " - but this is most likely not what you want."
                , messageKey);
        }
        
    }
}
