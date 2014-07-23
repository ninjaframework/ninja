package ninja.metrics;

import ninja.cache.CacheEhCacheImpl;

import com.google.inject.Inject;

public class InstrumentedEhCache extends InstrumentedCache {

    @Inject
    public InstrumentedEhCache(CacheEhCacheImpl cache) {
        super(cache);
    }

}
