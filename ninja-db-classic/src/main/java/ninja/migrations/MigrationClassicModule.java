package ninja.migrations;

import com.google.inject.AbstractModule;


public class MigrationClassicModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MigrationEngine.class).toProvider(MigrationEngineProvider.class);
        bind(MigrationInitializer.class).asEagerSingleton();
    }
    
}
