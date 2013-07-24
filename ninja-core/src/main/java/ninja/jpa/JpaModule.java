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
            String connectionUrl = ninjaProperties.getOrDie(NinjaConstant.DB_CONNECTION_URL);
            String connectionUsername = ninjaProperties.getOrDie(NinjaConstant.DB_CONNECTION_USERNAME);
            String connectionPassword = ninjaProperties.getOrDie(NinjaConstant.DB_CONNECTION_PASSWORD);
        
            // We are using hibernate, so we can set the connections stuff
            // via system properties:
            System.setProperty("hibernate.connection.url", connectionUrl);
            System.setProperty("hibernate.connection.username", connectionUsername);
            System.setProperty("hibernate.connection.password", connectionPassword);

            
            install(new JpaPersistModule(persistenceUnitName));
            bind(JpaInitializer.class).asEagerSingleton();
            
        }
        
        
    }

}
