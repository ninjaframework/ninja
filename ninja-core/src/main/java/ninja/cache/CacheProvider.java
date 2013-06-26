package ninja.cache;

import org.slf4j.Logger;

import ninja.utils.NinjaProperties;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class CacheProvider implements Provider<Cache> {

    private final NinjaProperties ninjaProperties;
    private final Injector injector;

    private Cache cache;
    private Logger logger;

    @Inject
    CacheProvider(Injector injector, 
                  NinjaProperties ninjaProperties, 
                  Logger logger) {
        this.ninjaProperties = ninjaProperties;
        this.injector = injector;
        this.logger = logger;
    }

    @Override
    public Cache get() {

        if (cache == null) {
            Class<? extends Cache> cacheClass = null;

            String cacheImplClassName = ninjaProperties.get(CacheConstant.cacheImplementation);
            if (cacheImplClassName != null) {
                try {

                    Class<?> clazz = Class.forName(cacheImplClassName);
                    cacheClass = clazz.asSubclass(Cache.class);

                    logger.info("cache.implementation is: " + cacheClass);

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class defined in configuration cache.implementation " +
                            "not found (" + cacheClass + ")", e);
                } catch (ClassCastException e) {
                    throw new RuntimeException("Class defined in configuration cache.implementation " +
                            "is not an instance of interface cache (" + cacheClass + ")", e);
                }
            }

            if (cacheClass == null) {

                    cacheClass = CacheEhCacheImpl.class;
                    logger.info("In produdction mode - using default Postoffice implementation "
                            + cacheClass);
                
            }

            cache = injector.getInstance(cacheClass);
        }
        return cache;
    }
}
