package ninja.conversion;

import com.google.inject.AbstractModule;

/**
 * Conversion module.
 */
public class ConversionModule extends AbstractModule {

    /**
     * Configure.
     */
    protected void configure() {
        this.bind(Conversion.class).toProvider(ConversionProvider.class).asEagerSingleton();
    }
}
