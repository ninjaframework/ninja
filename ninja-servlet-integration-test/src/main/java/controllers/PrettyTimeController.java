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

package controllers;

import java.util.Calendar;
import java.util.Date;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Lang;
import ninja.params.PathParam;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PrettyTimeController {

    @Inject
    Lang lang;

    public Result index(Context context) {
        // Only render the page. It contains some language specific strings.
        // It will use the requested language (or a fallback language)
        // from Accept-Language header
        Result result = Results.html();
        // just in case we set the language => we remove it...
        lang.clearLanguage(result);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        Date date = c.getTime();
        result.render("date", date);

        return result;
    }


    public Result indexWithLanguage(@PathParam("language") String language) {

        Result result = Results.ok().html().template("/views/PrettyTimeController/index.ftl.html");
        // This gets an url like /prettyTime/en
        // language is then the "en" part of the url.

        // We take that part and set that language as the default language
        // of the framework for this user.
        lang.setLanguage(language, result);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        Date date = c.getTime();
        result.render("date", date);

        return result;

    }

}
