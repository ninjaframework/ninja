/**
 * Copyright (C) 2012-2015 the original author or authors.
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

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.SimpleDate;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * PrettyTime integration for Ninja-FreeMarker.
 *
 * @author James Moger
 *
 */
public class TemplateEngineFreemarkerPrettyTimeMethod implements
        TemplateMethodModelEx {

    public final static Logger logger = LoggerFactory
            .getLogger(TemplateEngineFreemarkerPrettyTimeMethod.class);

    private final PrettyTime prettyTime;

    public TemplateEngineFreemarkerPrettyTimeMethod(Locale locale) {

        this.prettyTime = new PrettyTime(locale);

    }

    @Override
    public TemplateModel exec(List args) throws TemplateModelException {

        Date date = getFormattableObject(args.get(0));

        String result = prettyTime.format(date);

        return new SimpleScalar(result);

    }

    private Date getFormattableObject(Object value) {

        if (value instanceof SimpleDate) {
            return ((SimpleDate) value).getAsDate();
        } else {
            throw new RuntimeException(
                    "Formattable object for PrettyTime not found!");
        }
    }
}
