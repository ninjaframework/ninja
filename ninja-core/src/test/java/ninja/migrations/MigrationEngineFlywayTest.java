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

/*
  Copyright (C) 2012-2020 the original author or authors.
  <p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package ninja.migrations;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import ninja.BaseAndClassicModules;
import ninja.migrations.flyway.MigrationEngineFlyway;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;
import org.flywaydb.core.Flyway;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;

public class MigrationEngineFlywayTest {

    private MigrationEngineFlyway getTestInstance(Flyway flywayInstance,
                                                  NinjaMode ninjaMode,
                                                  Map<String, String> customProperties) {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(ninjaMode);
        ninjaProperties.setProperty(NinjaConstant.DB_CONNECTION_URL, "testurl");
        ninjaProperties.setProperty(NinjaConstant.DB_CONNECTION_USERNAME, "testuser");
        ninjaProperties.setProperty(NinjaConstant.DB_CONNECTION_PASSWORD, "testpassword");
        for (Map.Entry<String, String> property : customProperties.entrySet())
            ninjaProperties.setProperty(property.getKey(), property.getValue());

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new BaseAndClassicModules(ninjaProperties));
                bind(Flyway.class).toInstance(flywayInstance);
            }
        });
        return injector.getInstance(MigrationEngineFlyway.class);
    }

    private Flyway testMockedMigrate(NinjaMode ninjaMode, Map<String, String> customProperties) {
        Flyway mockedFlyway = mock(Flyway.class);
        MigrationEngineFlyway migrationEngineFlyway = getTestInstance(mockedFlyway, ninjaMode, customProperties);
        migrationEngineFlyway.migrate();
        return mockedFlyway;
    }

    private Flyway testMockedMigrate(NinjaMode ninjaMode) {
        return testMockedMigrate(ninjaMode, Collections.emptyMap());
    }

    @Test
    public void testModeDefault() {
        Flyway mock = testMockedMigrate(NinjaMode.test);
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock).setDataSource("testurl", "testuser", "testpassword");
        inOrder.verify(mock).clean();
        inOrder.verify(mock).migrate();
    }

    @Test
    public void testModeNoDrop() {
        Flyway mock = testMockedMigrate(NinjaMode.test,
                ImmutableMap.of(NinjaConstant.NINJA_MIGRATION_DROP_SCHEMA, String.valueOf(false)));
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock).migrate();
        verify(mock, never()).clean();
    }

    @Test
    public void prodModeDefault() {
        Flyway mock = testMockedMigrate(NinjaMode.prod);
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock).setDataSource("testurl", "testuser", "testpassword");
        inOrder.verify(mock).migrate();
        verify(mock, never()).clean();
    }

    @Test
    public void prodModeCustomLocation() {
        Flyway mock = testMockedMigrate(NinjaMode.prod,
                ImmutableMap.of(NinjaConstant.NINJA_MIGRATION_LOCATIONS, "location"));
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock).setLocations("location");
        inOrder.verify(mock).migrate();
    }

    @Test
    public void prodModeCustomSchema() {
        Flyway mock = testMockedMigrate(NinjaMode.prod,
                ImmutableMap.of(NinjaConstant.NINJA_MIGRATION_SCHEMAS, "schema"));
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock).setSchemas("schema");
        inOrder.verify(mock).migrate();
    }
}
