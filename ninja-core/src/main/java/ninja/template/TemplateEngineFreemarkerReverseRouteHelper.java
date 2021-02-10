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

import java.util.ArrayList;
import java.util.List;

import ninja.Router;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

@Singleton
public class TemplateEngineFreemarkerReverseRouteHelper {

    final Router router;

    @Inject
    public TemplateEngineFreemarkerReverseRouteHelper(Router router) {
        this.router = router;

    }

    public TemplateModel computeReverseRoute(List args) throws TemplateModelException {

        if (args.size() < 2) {

            throw new TemplateModelException(
                    "Please specify at least classname and controller (2 parameters).");

        } else {

            List<String> strings = new ArrayList<>(args.size());

            for (Object o : args) {

                // We currently allow only numbers and strings as arguments
                if (o instanceof String) {
                    strings.add((String) o);
                } if (o instanceof SimpleScalar) {
                    strings.add(((SimpleScalar) o).getAsString());
                } else if (o instanceof SimpleNumber) {
                    strings.add(((SimpleNumber) o).toString());
                }

            }

            try {

                Class<?> clazz = Class.forName(strings.get(0));
                
                Object [] parameterMap = strings.subList(2, strings.size()).toArray();

                String reverseRoute = router.getReverseRoute(
                        clazz,
                        strings.get(1),
                        parameterMap);

                return new SimpleScalar(reverseRoute);
            } catch (ClassNotFoundException ex) {
                throw new TemplateModelException("Error. Cannot find class for String: " + strings.get(0));
            }
        }

    }
}
