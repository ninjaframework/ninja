package ninja.jpa;

import javax.inject.Inject;

import ninja.lifecycle.Start;

import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;

/**
 * Initializes the guice-persist JPA support.
 * 
 * Please note that we are using @Start annotation of Ninja
 * to start up the service.
 * 
 * @author ra
 *
 */
@Singleton
public class JpaInitializer {
    
    private PersistService persistService;


    @Inject
    JpaInitializer(PersistService persistService) {
        

        this.persistService = persistService;     

    }
    
    
    @Start(order = 10)
    public void start() {
        
        persistService.start();        
        // At this point JPA is started and ready.
        
    }

}
