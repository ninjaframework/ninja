package ninja.jpa;

import javax.inject.Inject;

import ninja.migrations.MigrationEngine;

import com.google.inject.persist.PersistService;

public class JpaInitializer {
    
    @Inject
    JpaInitializer(
                  PersistService persistService,
                  MigrationEngine migrationEngine) {
        

        // migrate the database if needed
        migrationEngine.migrate();
        
        // then start persistence
        persistService.start();
        
        // At this point JPA is started and ready.
    }

}
