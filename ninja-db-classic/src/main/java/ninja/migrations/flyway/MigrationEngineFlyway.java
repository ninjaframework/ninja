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

package ninja.migrations.flyway;

import com.google.inject.Provider;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.flywaydb.core.Flyway;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.migrations.MigrationEngine;
import org.flywaydb.core.api.configuration.FluentConfiguration;

@Singleton
public class MigrationEngineFlyway implements MigrationEngine {

    private final NinjaProperties ninjaProperties;
    private final Provider<FluentConfiguration> fluentConfigurationProvider;

    @Inject
    public MigrationEngineFlyway(NinjaProperties ninjaProperties,
                                 Provider<FluentConfiguration> fluentConfigurationProvider) {
        this.ninjaProperties = ninjaProperties;
        this.fluentConfigurationProvider = fluentConfigurationProvider;
    }
    
    @Override
    public void migrate() {  
        // Get the connection credentials from application.conf
        String connectionUrl = ninjaProperties.getOrDie(NinjaConstant.DB_CONNECTION_URL);
        String connectionUsername = ninjaProperties.getOrDie(NinjaConstant.DB_CONNECTION_USERNAME);
        String connectionPassword = ninjaProperties.getOrDie(NinjaConstant.DB_CONNECTION_PASSWORD);
        String locations = ninjaProperties.get(NinjaConstant.NINJA_MIGRATION_LOCATIONS);
        String schemas = ninjaProperties.get(NinjaConstant.NINJA_MIGRATION_SCHEMAS);

        // We migrate automatically => if you do not want that (eg in production)
        // set ninja.migration.run=false in application.conf
        FluentConfiguration fluentConfiguration = fluentConfigurationProvider.get();
        if (schemas != null) {
            fluentConfiguration.schemas(schemas);
        }
        if (locations != null) {
            fluentConfiguration.locations(locations);
        }

        Flyway flyway = fluentConfiguration.dataSource(connectionUrl, connectionUsername, connectionPassword).load();

        // In testmode we are cleaning the database so that subsequent testcases
        // get a fresh database.
        if (ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_MIGRATION_DROP_SCHEMA,
                ninjaProperties.isTest())) {
            flyway.clean();
        }

        flyway.migrate();
    }

}