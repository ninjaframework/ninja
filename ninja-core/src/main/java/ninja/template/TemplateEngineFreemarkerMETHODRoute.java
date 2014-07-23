/**
 * Copyright (C) 2012-2014 the original author or authors.
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
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public abstract class TemplateEngineFreemarkerMETHODRoute {

    private final Router router;

    private final String httpMethod;

    private final int minimumArgsCount;

    public TemplateEngineFreemarkerMETHODRoute(Router router, String method, int minArgsCount) {
        this.router = router;
        this.httpMethod = method;
        this.minimumArgsCount = minArgsCount;
    }

    public TemplateModel computeReverseRoute(List args) throws TemplateModelException {

        if (args.size() < minimumArgsCount) {

            throw new TemplateModelException(
                    "Please specify the controller and parameters.");

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

                String reverseRoute;
                Class<?> clazz = Class.forName(strings.get(0));

                if (strings.size() == 1) {
                    reverseRoute = router.getReverseMETHOD(httpMethod, clazz);
                } else {
                    Object [] parameterMap = strings.subList(1, strings.size()).toArray();

                    if (parameterMap.length % 2 != 0) {
                        throw new TemplateModelException("Odd parameter count! Always provide key-value pairs for reverse route generation.");
                    }

                    reverseRoute = router.getReverseMETHOD(httpMethod, clazz, parameterMap);
                }

                return new SimpleScalar(reverseRoute);
            } catch (ClassNotFoundException ex) {
                throw new TemplateModelException("Error. Cannot find class for String: " + strings.get(0));
            }
        }

    }
}
