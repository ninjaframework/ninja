package ninja.migrations;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.flyway.core.Flyway;

@Singleton
public class MigrationEngineFlyway implements MigrationEngine {

    private NinjaProperties ninjaProperties;

    @Inject
    public MigrationEngineFlyway(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
        
    }
    
    @Override
    public void migrate() {
        
        // Run migration only when activated in configuration.conf:
        Boolean runMigrations = ninjaProperties.getBoolean(NinjaConstant.NINJA_MIGRATION_RUN);
        
        if (runMigrations != null && runMigrations) {
        
            // Get the connection credentials from application.conf
            String connectionUrl = ninjaProperties.getOrDie(NinjaConstant.DB_CONNECTION_URL);
            String connectionUsername = ninjaProperties.getOrDie(NinjaConstant.DB_CONNECTION_USERNAME);
            String connectionPassword = ninjaProperties.getOrDie(NinjaConstant.DB_CONNECTION_PASSWORD);
        
            // We migrate automatically => if you do not want that (eg in production)
            // set ninja.migration.run=false in application.conf
            Flyway flyway = new Flyway();
            flyway.setDataSource(connectionUrl, connectionUsername, connectionPassword);
            flyway.migrate();
            
        }
        
    }

    

}
