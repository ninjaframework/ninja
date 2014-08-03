/*
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

package testapplication.conf;

import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import router.AnnotationRouter;

import com.google.inject.Inject;

public class Routes implements ApplicationRoutes {

    /**
     * Path of the entities
     */
    public static final String CONTROLLERS_PACKAGE = "responders";


    Logger logger = LoggerFactory.getLogger(Routes.class);

    private NinjaProperties ninjaProperties;

    @Inject
    AnnotationRouter annotationRouter;

    @Inject
    public Routes(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;

    }

    /**
     * Using a (almost) nice DSL we can configure the router.
     *
     * The second argument NinjaModuleDemoRouter contains all routes of a
     * submodule. By simply injecting it we activate the routes.
     *
     * @param router
     *            The default router of this application
     */
    @Override
    public void init(Router router) {

        annotationRouter.init(router);

        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");

    }


}