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

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import ninja.migrations.flyway.MigrationEngineFlyway;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;
import ninja.utils.NinjaProperties;

import static org.mockito.Mockito.*;

public class MigrationEngineFlywayTest {
	
	Flyway mockedFlyway;

    private MigrationEngineFlyway getTestInstance(FluentConfiguration fluentInstance,
                                                  NinjaMode ninjaMode,
                                                  Map<String, String> customProperties) {
        NinjaPropertiesImpl ninjaProperties = NinjaPropertiesImpl.builder()
                .withMode(ninjaMode)
                .build();
        ninjaProperties.setProperty(NinjaConstant.DB_CONNECTION_URL, "testurl");
        ninjaProperties.setProperty(NinjaConstant.DB_CONNECTION_USERNAME, "testuser");
        ninjaProperties.setProperty(NinjaConstant.DB_CONNECTION_PASSWORD, "testpassword");
        for (Map.Entry<String, String> property : customProperties.entrySet())
            ninjaProperties.setProperty(property.getKey(), property.getValue());

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NinjaProperties.class).toInstance(ninjaProperties);
                install(new MigrationClassicModule());
                bind(FluentConfiguration.class).toInstance(fluentInstance);
                
            }
        });
        return injector.getInstance(MigrationEngineFlyway.class);
    }

    private FluentConfiguration testMockedMigrate(NinjaMode ninjaMode, Map<String, String> customProperties) {
    	FluentConfiguration mockedFluent = mock(FluentConfiguration.class);
    	mockedFlyway = mock(Flyway.class);
    	when(mockedFluent.load()).thenReturn(mockedFlyway);
    	when(mockedFluent.dataSource("testurl", "testuser", "testpassword")).thenReturn(mockedFluent);
        MigrationEngineFlyway migrationEngineFlyway = getTestInstance(mockedFluent, ninjaMode, customProperties);
        migrationEngineFlyway.migrate();
        return mockedFluent;
    }

    private FluentConfiguration testMockedMigrate(NinjaMode ninjaMode) {
        return testMockedMigrate(ninjaMode, Collections.emptyMap());
    }

    @Test
    public void testModeDefault() {
    	FluentConfiguration mock = testMockedMigrate(NinjaMode.test);
        InOrder inOrder = Mockito.inOrder(mock, mockedFlyway);

        inOrder.verify(mock).dataSource("testurl", "testuser", "testpassword");
        inOrder.verify(mock).load();
        
        inOrder.verify(mockedFlyway).clean();
        inOrder.verify(mockedFlyway).migrate();
    }

  
    @Test
    public void testModeNoDrop() {
    	FluentConfiguration mock = testMockedMigrate(NinjaMode.test,
                ImmutableMap.of(NinjaConstant.NINJA_MIGRATION_DROP_SCHEMA, String.valueOf(false)));
        InOrder inOrder = Mockito.inOrder(mock, mockedFlyway);

        inOrder.verify(mock).dataSource("testurl", "testuser", "testpassword");
        inOrder.verify(mock).load();
        
        inOrder.verify(mockedFlyway).migrate();
        verify(mockedFlyway, never()).clean();
    }
    
   

    @Test
    public void prodModeDefault() {
    	FluentConfiguration mock = testMockedMigrate(NinjaMode.prod);
        InOrder inOrder = Mockito.inOrder(mock, mockedFlyway);

        inOrder.verify(mock).dataSource("testurl", "testuser", "testpassword");
        inOrder.verify(mock).load();
        
        inOrder.verify(mockedFlyway).migrate();
        verify(mockedFlyway, never()).clean();
    }

    @Test
    public void prodModeCustomLocation() {
    	FluentConfiguration mock = testMockedMigrate(NinjaMode.prod,
                ImmutableMap.of(NinjaConstant.NINJA_MIGRATION_LOCATIONS, "location"));
    	InOrder inOrder = Mockito.inOrder(mock, mockedFlyway);
        inOrder.verify(mock).locations("location");
        inOrder.verify(mockedFlyway).migrate();
    }

    
    @Test
    public void prodModeCustomSchema() {
    	FluentConfiguration mock = testMockedMigrate(NinjaMode.prod,
                ImmutableMap.of(NinjaConstant.NINJA_MIGRATION_SCHEMAS, "schema"));
    	InOrder inOrder = Mockito.inOrder(mock, mockedFlyway);
        inOrder.verify(mock).schemas("schema");
        inOrder.verify(mockedFlyway).migrate();
    }
    
}
