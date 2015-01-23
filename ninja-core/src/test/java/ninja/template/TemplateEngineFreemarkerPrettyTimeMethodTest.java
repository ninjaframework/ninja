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

import static org.junit.Assert.assertThat;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import freemarker.template.SimpleDate;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;

/**
 *
 * @author James Moger
 */
public class TemplateEngineFreemarkerPrettyTimeMethodTest {

    Map<Locale, String> expections = new HashMap<Locale, String>() {

        private static final long serialVersionUID = 1L;

        {
            put(Locale.ENGLISH, "1 day ago");
            put(Locale.GERMAN, "vor 1 Tag");
            put(Locale.FRENCH, "il y a 1 jour");
            put(Locale.ITALIAN, "1 giorno fa");
            put(Locale.CHINESE, "1 天 前");
            put(Locale.JAPANESE, "1 日 前");
            put(Locale.KOREAN, "1일 전");
        }
    };

    @Test
    public void testThatJavaUtilDateWorks() throws Exception {

        test(new SimpleDate(new java.util.Date(getTime()), SimpleDate.DATE));
    }

    @Test
    public void testThatJavaSqlDateWorks() throws Exception {

        test(new SimpleDate(new Date(getTime())));
    }

    @Test
    public void testThatJavaSqlTimeWorks() throws Exception {

        test(new SimpleDate(new Time(getTime())));
    }

    @Test
    public void testThatJavaSqlTimestampWorks() throws Exception {

        test(new SimpleDate(new Timestamp(getTime())));
    }

    private long getTime() {
        // return yesterday
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        return c.getTimeInMillis();
    }

    public void test(SimpleDate simpleDate) throws Exception {

        for (Map.Entry<Locale, String> entry : expections.entrySet()) {

            Locale locale = entry.getKey();
            String expected = entry.getValue();

            TemplateEngineFreemarkerPrettyTimeMethod method = new TemplateEngineFreemarkerPrettyTimeMethod(
                    locale);

            List args = new ArrayList();
            args.add(simpleDate);

            TemplateModel returnValue = method.exec(args);

            assertThat(((SimpleScalar) returnValue).getAsString(),
                    CoreMatchers.equalTo(expected));
        }
    }
}
