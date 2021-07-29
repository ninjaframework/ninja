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

package ninja.migrations;

import ninja.utils.ImplFromPropertiesFactory;

import ninja.utils.NinjaProperties;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import ninja.utils.NinjaConstant;
import org.slf4j.LoggerFactory;

/**
 * A provider gets a Flyway configuration object.
 */
@Singleton
public class FluentConfigurationProvider implements Provider<FluentConfiguration> {
    
     
    @Inject
    public FluentConfigurationProvider(Injector injector, NinjaProperties ninjaProperties) {
        
    }

    @Override
    public FluentConfiguration get() {
        return Flyway.configure();
    }
   
}