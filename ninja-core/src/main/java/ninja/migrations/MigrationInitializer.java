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

import ninja.lifecycle.Start;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

/**
 * This class must be bound in a Guice module so that
 * @Start annotation works
 * 
 * It starts migrations by the registered migration engine.
 * 
 * @author ra
 *
 */
@Singleton
public class MigrationInitializer {

    private final Provider<MigrationEngine> migrationEngineProvider;
    private final NinjaProperties ninjaProperties;
    
    @Inject
    public MigrationInitializer(NinjaProperties ninjaProperties,
                                Provider<MigrationEngine> migrationEngineProvider) {
        this.ninjaProperties = ninjaProperties;
        this.migrationEngineProvider = migrationEngineProvider;
    }
    
    /**
     * We start it at order 9 which is below order 10 (where JPA is started)
     */
    @Start(order=9)
    public void start() {
        // Run migration only when activated in configuration.conf:
        Boolean runMigrations = ninjaProperties.getBoolean(NinjaConstant.NINJA_MIGRATION_RUN);
        
        if (runMigrations != null && runMigrations) {
            // provider used for late binding
            migrationEngineProvider.get().migrate();
        }
    }

}
