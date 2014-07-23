package ninja.metrics;

import ninja.cache.CacheMemcachedImpl;

import com.google.inject.Inject;

public class InstrumentedMemcached extends InstrumentedCache {

    @Inject
    public InstrumentedMemcached(CacheMemcachedImpl cache) {
        super(cache);
    }

}
