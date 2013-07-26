package ninja.migrations;

import com.google.inject.ImplementedBy;

@ImplementedBy(MigrationEngineFlyway.class)
public interface MigrationEngine {
    
    /**
     * Runs the migration scripts.
     * 
     * But only when ninja.migration.run=true
     * 
     * Implementations of migrate MUST BE safe in terms of multithreading and multiple instances 
     * potentially running migrations at the same time...
     * 
     */
    public void migrate();

}
