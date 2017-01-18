/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.jaxy.JaxyRoutes;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class Routes implements ApplicationRoutes {

    Logger logger = LoggerFactory.getLogger(Routes.class);

    private NinjaProperties ninjaProperties;

    @Inject
    public Routes(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;

    }

    @Override
    public void init(Router router) {

        JaxyRoutes jaxyRoutes = new JaxyRoutes(ninjaProperties);
        jaxyRoutes.init(router);

    }

}