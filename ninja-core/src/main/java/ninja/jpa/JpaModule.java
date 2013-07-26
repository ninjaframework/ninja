package ninja.jpa;

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
        
            // We are using Hibernate, so we can set the connections stuff
            // via system properties:
            if (connectionUrl != null) {
                System.setProperty("hibernate.connection.url", connectionUrl);
            }
            
            if (connectionUsername != null) {                
                System.setProperty("hibernate.connection.username", connectionUsername);
            }
            
            if (connectionPassword != null) {                
                System.setProperty("hibernate.connection.password", connectionPassword);
            }

            // Now - it may be the case the neither connection.url, connection.username nor
            // connection.password is set. But this may be okay e.g. when using JDNI to
            // configure your datasources...
            
            install(new JpaPersistModule(persistenceUnitName));
            bind(JpaInitializer.class).asEagerSingleton();
            
        }
        
        
    }

}
