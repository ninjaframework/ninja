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

package ninja.jpa;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import java.util.Properties;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

public class JpaModule extends AbstractModule {
    
    NinjaProperties ninjaProperties;
    
    public JpaModule(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    protected void configure() {
        
        ///////////////////////////////////////////////////////////////
        // only start up Jpa when it is configured in application.conf
        ///////////////////////////////////////////////////////////////
        String persistenceUnitName = ninjaProperties.get(
                NinjaConstant.PERSISTENCE_UNIT_NAME);
        
        if (persistenceUnitName != null) {
        
            // Get the connection credentials from application.conf
            String connectionUrl = ninjaProperties.get(NinjaConstant.DB_CONNECTION_URL);
            String connectionUsername = ninjaProperties.get(NinjaConstant.DB_CONNECTION_USERNAME);
            String connectionPassword = ninjaProperties.get(NinjaConstant.DB_CONNECTION_PASSWORD);
        
            Properties jpaProperties = new Properties();
            
            // We are using Hibernate, so we can set the connections stuff
            // via system properties:
            if (connectionUrl != null) {
                jpaProperties.put("hibernate.connection.url", connectionUrl);
            }
            
            if (connectionUsername != null) {                
                jpaProperties.put("hibernate.connection.username", connectionUsername);
            }
            
            if (connectionPassword != null) {                
                jpaProperties.put("hibernate.connection.password", connectionPassword);
            }

            // Now - it may be the case the neither connection.url, connection.username nor
            // connection.password is set. But this may be okay e.g. when using JDNI to
            // configure your datasources...
            install(new JpaPersistModule(persistenceUnitName).properties(jpaProperties));
            
            
            UnitOfWorkInterceptor unitOfWorkInterceptor = new UnitOfWorkInterceptor();
        
            requestInjection(unitOfWorkInterceptor);

            // class-level @UnitOfWork
            bindInterceptor(
                annotatedWith(UnitOfWork.class),
                any(),
                unitOfWorkInterceptor);
            
            // method-level @UnitOfWork
            bindInterceptor(
                any(),
                annotatedWith(UnitOfWork.class),
                unitOfWorkInterceptor);
            
            
            bind(JpaInitializer.class).asEagerSingleton();
            
            
            
        }
        
        
    }

}
