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

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import ninja.utils.NinjaConstant;
import org.slf4j.LoggerFactory;

/**
 * A provider that determines which implementation to load as a MigrationEngine based on 
 * the value of a configuration key in {@link NinjaProperties} (aka application.conf).
 * 
 * The configured implementation is only resolved when its actually used vs.
 * at application startup.
 * 
 * If this variable is set the instance for that class is 
 * instantiated and used as MigrationEngine implementation.
 * 
 * If the variable is not set {@link MigrationEngineFlyway} is used by default.
 */
@Singleton
public class MigrationEngineProvider implements Provider<MigrationEngine> {
    static private final Logger logger = LoggerFactory.getLogger(MigrationEngineProvider.class);
    
    private final ImplFromPropertiesFactory<MigrationEngine> factory;
    
    @Inject
    public MigrationEngineProvider(Injector injector, NinjaProperties ninjaProperties) {
        this.factory = new ImplFromPropertiesFactory<>(
            injector,
            ninjaProperties,
            NinjaConstant.MIGRATION_ENGINE_IMPLEMENTATION,
            MigrationEngine.class,
            "ninja.migrations.flyway.MigrationEngineFlyway",
            true,
            logger);
    }

    @Override
    public MigrationEngine get() {
        return factory.create();
    }
   
}