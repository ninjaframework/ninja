/**
 * Copyright (C) 2012-2019 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import ninja.AssetsController;
import ninja.Router;
import ninja.utils.NinjaProperties;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

@Singleton
public class TemplateEngineFreemarkerWebJarsAtMethod implements
        TemplateMethodModelEx {

    final static String APPLICATION_WEBJARS_PREFER_CDN = "application.webjars.prefer_cdn";

    final static String JSDELIVER_CDN = "//cdn.jsdelivr.net/webjars/";

    final boolean useCDN;
    final Router router;
    final TemplateEngineFreemarkerReverseRouteHelper templateEngineFreemarkerReverseRouteHelper;

    @Inject
    public TemplateEngineFreemarkerWebJarsAtMethod(
            NinjaProperties ninjaProperties,
            Router router,
            TemplateEngineFreemarkerReverseRouteHelper templateEngineFreemarkerReverseRouteHelper) {
        
        this.useCDN = ninjaProperties.getBooleanWithDefault(APPLICATION_WEBJARS_PREFER_CDN, false);
        this.router = router;
        this.templateEngineFreemarkerReverseRouteHelper = templateEngineFreemarkerReverseRouteHelper;

    }

    public TemplateModel exec(List args) throws TemplateModelException {

        if (args.size() == 0) {
            throw new TemplateModelException("Error. You must specify a webjars asset URL.");
        }

        // Allow a boolean as second argument to force webjars usage
        if (useCDN && (args.size() == 1 || !((TemplateBooleanModel) args.get(1)).getAsBoolean())) {

            String filename = ((SimpleScalar) args.get(0)).getAsString();
            return new SimpleScalar(JSDELIVER_CDN + filename);

        } else {

            List argsWithControllerAndMethod = new ArrayList();
            argsWithControllerAndMethod.add(AssetsController.class.getName());
            argsWithControllerAndMethod.add("serveWebJars");
            argsWithControllerAndMethod.add("fileName");
            argsWithControllerAndMethod.addAll(args);

            return templateEngineFreemarkerReverseRouteHelper.computeReverseRoute(
                    argsWithControllerAndMethod);
       
        }

    }
}
