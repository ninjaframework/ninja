/**
 * Copyright (C) 2012- the original author or authors.
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

import ninja.Result;
import ninja.Route;
import ninja.utils.NinjaConstant;

/**
 * Helper methods for template engines
 * 
 * @author James Roper
 */
public class TemplateEngineHelper {

    public String getTemplateForResult(Route route, Result result, String suffix) {
        if (result.getTemplate() == null) {
            Class controller = route.getControllerClass();

            // Calculate the correct path of the template.
            // We always assume the template in the subdir "views"

            // 1) If we are in the main project =>
            // /controllers/ControllerName
            // to
            // /views/ControllerName/templateName.ftl.html
            // 2) If we are in a plugin / subproject
            // =>
            // /controllers/some/packages/submoduleName/ControllerName
            // to
            // views/some/packages/submoduleName/ControllerName/templateName.ftl.html

            // So let's calculate the parent package of the controller:
            String controllerPackageName = controller.getPackage().getName();
            // This results in something like controllers or
            // some.package.controllers

            // Replace controller prefix with views prefix
            String parentPackageOfController = controllerPackageName
                    .replaceFirst(NinjaConstant.CONTROLLERS_DIR,
                            NinjaConstant.VIEWS_DIR);

            // And now we rewrite everything from "." notation to directories /
            String parentControllerPackageAsPath = parentPackageOfController
                    .replaceAll("\\.", "/");

            // and the final path of the controller will be something like:
            // views/some/package/submoduleName/ControllerName/templateName.ftl.html
            return String.format("/%s/%s/%s%s", parentControllerPackageAsPath,
                    controller.getSimpleName(), route.getControllerMethod()
                            .getName(), suffix);
        } else {
            return result.getTemplate();
        }
    }

}
