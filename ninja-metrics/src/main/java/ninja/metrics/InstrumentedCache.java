/**
 * Copyright (C) 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.metrics;

import java.util.Map;

import ninja.cache.Cache;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.inject.Inject;

/**
 * Instruments the configured Ninja Cache instance for Metrics collection.
 *
 * @author James Moger
 *
 */
abstract class InstrumentedCache implements Cache {

    private final Cache underlyingCache;

    @Inject
    private MetricsService metricsService;

    private Counter hitCounter;

    private Counter missCounter;

    public InstrumentedCache(Cache cache) {
        this.underlyingCache = cache;
        init();
    }

    public void init() {
        MetricRegistry registry = metricsService
            .getMetricRegistry(MetricsService.METRICS_REGISTRY_CACHE);

        hitCounter = getCounter(registry, "hits");
        missCounter = getCounter(registry, "miss");
    }

    protected Timer getTimer(MetricRegistry registry, String name) {
        return registry.timer(MetricRegistry.name(underlyingCache.getClass(),
                name));
    }

    protected Counter getCounter(MetricRegistry registry, String name) {
        return registry.counter(MetricRegistry.name(underlyingCache.getClass(),
                name));
    }

    @Override
    @Timed
    public void add(String key, Object value, int expiration) {
        underlyingCache.add(key, value, expiration);
    }

    @Override
    @Timed
    public boolean safeAdd(String key, Object value, int expiration) {
        return underlyingCache.safeAdd(key, value, expiration);
    }

    @Override
    @Timed
    public void set(String key, Object value, int expiration) {
        underlyingCache.set(key, value, expiration);
    }

    @Override
    @Timed
    public boolean safeSet(String key, Object value, int expiration) {
        return underlyingCache.safeSet(key, value, expiration);
    }

    @Override
    @Timed
    public void replace(String key, Object value, int expiration) {
        underlyingCache.replace(key, value, expiration);
    }

    @Override
    @Timed
    public boolean safeReplace(String key, Object value, int expiration) {
        return underlyingCache.safeReplace(key, value, expiration);
    }

    @Override
    @Timed
    public Object get(String key) {
        Object result = underlyingCache.get(key);
        if (result == null) {
            missCounter.inc();
        } else {
            hitCounter.inc();
        }
        return result;
    }

    @Override
    @Timed
    public Map<String, Object> get(String[] keys) {
        Map<String, Object> result = underlyingCache.get(keys);
        if (result == null || result.isEmpty()) {
            missCounter.inc(keys.length);
        } else {
            hitCounter.inc(result.size());
            missCounter.inc(keys.length - result.size());
        }
        return result;
    }

    @Override
    @Timed
    public long incr(String key, int by) {
        return underlyingCache.incr(key, by);

    }

    @Override
    @Timed
    public long decr(String key, int by) {
        return underlyingCache.decr(key, by);
    }

    @Override
    @Timed
    public void clear() {
        underlyingCache.clear();
    }

    @Override
    @Timed
    public void delete(String key) {
        underlyingCache.delete(key);
    }

    @Override
    @Timed
    public boolean safeDelete(String key) {
        return underlyingCache.safeDelete(key);
    }
}
