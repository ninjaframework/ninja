package ninja.jpa;

import javax.inject.Inject;

import com.google.inject.persist.PersistService;

public class JpaInitializer {
    
    @Inject
    JpaInitializer(
                  //@One PersistService service, @Two PersistService service2
                  PersistService persistService) {
        
//        Flyway flyway = new Flyway();
//
//        // Point it to the database
//        flyway.setDataSource("jdbc:hsqldb:.", "sa", null);
//
//        // Start the migration
//        flyway.migrate();        
        System.out.println("starting!");

        persistService.start();
        // At this point JPA is started and ready.
    }

}
