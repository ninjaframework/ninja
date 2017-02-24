/*
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Optional;

@Singleton
public class NinjaBaseDirectoryResolver {

    private final Optional<String> applicationModulesBasePackage;

    @Inject
    public NinjaBaseDirectoryResolver(NinjaProperties ninjaProperties) {
        // custom base package for application modules (e.g. com.example.conf.Routes)
        this.applicationModulesBasePackage
                = Optional.ofNullable(ninjaProperties.get(
                        NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE));
    }

    public String resolveApplicationClassName(String classLocationAsDefinedByNinja) {
        if (applicationModulesBasePackage.isPresent()) {
            return new StringBuilder()
                    .append(applicationModulesBasePackage.get())
                    .append('.')
                    .append(classLocationAsDefinedByNinja)
                    .toString();
        } else {
            return classLocationAsDefinedByNinja;
        }
    }

}
