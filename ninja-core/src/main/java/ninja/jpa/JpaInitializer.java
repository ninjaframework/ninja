package ninja.jpa;

import javax.inject.Inject;

import ninja.migrations.MigrationEngine;

import com.google.inject.persist.PersistService;

public class JpaInitializer {
    
    @Inject
    JpaInitializer(
                  //@One PersistService service, @Two PersistService service2
                  PersistService persistService,
                  MigrationEngine migrationEngine) {
        

        // migrate the database
        migrationEngine.migrate();
        
        // then start persistence
        persistService.start();
        
        // At this point JPA is started and ready.
    }

}
