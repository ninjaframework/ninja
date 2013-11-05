package ninja;

import ninja.jpa.JpaInitializer;
import ninja.jpa.JpaModule;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Base class for testing JPA-based DAOs
 * 
 * How to use: Extend the Class and call getInstace(Class<SomeDao>) method to
 * get a real DAO. Then use JUnit assertions to test it (Example:
 * assertEquals(0,someDao.getAll().size());)
 * 
 * @author emiguelt
 * 
 */
public abstract class NinjaDaoTestBase {
    /**
     * Persistence Service initializer
     */
    private JpaInitializer jpaInitializer;
    /**
     * Guice Injector to get DAOs
     */
    private Injector injector;

    /**
     * Constructor checks if NinjaMode was set in System properties, if not,
     * NinjaMode.test is used as default
     */
    public NinjaDaoTestBase() {
        Optional<NinjaMode> mode = NinjaModeHelper
                .determineModeFromSystemProperties();
        init(mode.isPresent() ? mode.get() : NinjaMode.test);

    }

    private void init(NinjaMode testMode) {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(testMode);
        injector = Guice.createInjector(new JpaModule(ninjaProperties));
        jpaInitializer = injector.getInstance(JpaInitializer.class);
        jpaInitializer.start();
    }

    /**
     * Constructor, receives the test mode to choose the database and
     * initializes the Persistence service
     * 
     * @param testMode
     */
    public NinjaDaoTestBase(NinjaMode testMode) {
        init(testMode);
    }

    /**
     * Get the DAO instances ready to use
     * 
     * @param clazz
     * @return DAO
     */
    protected <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

}
