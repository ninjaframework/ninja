package ninja.cache;

import static org.junit.Assert.assertTrue;
import ninja.Configuration;
import ninja.lifecycle.LifecycleSupport;
import ninja.utils.NinjaPropertiesImpl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class CacheProviderTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    

    @Test
    public void testEhCacheDefaulLoadingWorks() {

        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl();   
        
        ninjaProperties.setProperty(CacheConstant.CACHE_IMPLEMENTATION, null);
        
        Injector injector = Guice.createInjector(new Configuration(ninjaProperties), LifecycleSupport.getModule());
        
        Logger logger = injector.getInstance(Logger.class);
        
        CacheProvider cacheProvider = new CacheProvider(
                injector, 
                ninjaProperties, 
                logger);
        
        assertTrue(cacheProvider.get() instanceof CacheEhCacheImpl);
         
    }
    
    
    @Test
    public void testMemcachedLoadingWorks() {

        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl();   
        
        ninjaProperties.setProperty(CacheConstant.CACHE_IMPLEMENTATION, CacheMemcachedImpl.class.getName());
        // just a dummy to test that loading works
        ninjaProperties.setProperty(CacheMemcachedImpl.MEMCACHED_HOST, "127.0.0.1:1234");
        
        Injector injector = Guice.createInjector(new Configuration(ninjaProperties), LifecycleSupport.getModule());
        
        Logger logger = injector.getInstance(Logger.class);
        
        CacheProvider cacheProvider = new CacheProvider(
                injector, 
                ninjaProperties, 
                logger);
        
        assertTrue(cacheProvider.get() instanceof CacheMemcachedImpl);
         
    }
    
    
    @Test
    public void testThatSettingWrongCacheImplementationYieldsException() {

        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl();   
        
        ninjaProperties.setProperty(CacheConstant.CACHE_IMPLEMENTATION, "not_existing_implementation");
        
        Injector injector = Guice.createInjector(new Configuration(ninjaProperties), LifecycleSupport.getModule());
        
        Logger logger = injector.getInstance(Logger.class);
        
        // this will not work => we expect a runtime exception...
        thrown.expect(RuntimeException.class);
        CacheProvider cacheProvider = new CacheProvider(
                injector, 
                ninjaProperties, 
                logger);
        
        cacheProvider.get();
        
    }

}
