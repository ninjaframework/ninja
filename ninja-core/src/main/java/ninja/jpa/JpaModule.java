package ninja.jpa;

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
                JpaConstant.PERSISTENCE_UNIT_NAME);
        
        if (persistenceUnitName != null) {
            
            install(new JpaPersistModule(persistenceUnitName));
            bind(JpaInitializer.class).asEagerSingleton();
            
        }
        
        
    }

}
