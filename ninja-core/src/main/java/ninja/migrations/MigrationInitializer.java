package ninja.migrations;

import ninja.lifecycle.Start;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class must be bound in a Guice module so that
 * @Start annotation works
 * 
 * It starts migrations by the registered migration engine.
 * 
 * @author ra
 *
 */
@Singleton
public class MigrationInitializer {

    private MigrationEngine migrationEngine;

    @Inject
    public MigrationInitializer(MigrationEngine migrationEngine) {
        this.migrationEngine = migrationEngine;
      

    }
    
    /**
     * We start it at order 9 which is below order 10 (where JPA is started)
     */
    @Start(order=9)
    public void start() {
        
        migrationEngine.migrate();
        
    }

}
