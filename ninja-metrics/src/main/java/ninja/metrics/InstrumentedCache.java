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

    private Timer addTimer;

    private Timer safeAddTimer;

    private Timer setTimer;

    private Timer safeSetTimer;

    private Timer replaceTimer;

    private Timer safeReplaceTimer;

    private Timer getTimer;

    private Timer getManyTimer;

    private Timer incrTimer;

    private Timer decrTimer;

    private Timer clearTimer;

    private Timer deleteTimer;

    private Timer safeDeleteTimer;

    private Counter hitCounter;

    private Counter missCounter;

    public InstrumentedCache(Cache cache) {
        this.underlyingCache = cache;
    }

    public void init() {
        MetricRegistry registry = metricsService
                .getMetricRegistry(MetricsService.METRICS_REGISTRY_CACHE);

        addTimer = getTimer(registry, "add");
        safeAddTimer = getTimer(registry, "safeAdd");
        setTimer = getTimer(registry, "set");
        safeSetTimer = getTimer(registry, "safeSet");
        replaceTimer = getTimer(registry, "replace");
        safeReplaceTimer = getTimer(registry, "safeReplace");
        getTimer = getTimer(registry, "get");
        getManyTimer = getTimer(registry, "getMany");
        incrTimer = getTimer(registry, "incr");
        decrTimer = getTimer(registry, "decr");
        clearTimer = getTimer(registry, "clear");
        deleteTimer = getTimer(registry, "delete");
        safeDeleteTimer = getTimer(registry, "safeDelete");

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
    public void add(String key, Object value, int expiration) {
        init();
        final Timer.Context ctx = addTimer.time();
        try {
            underlyingCache.add(key, value, expiration);
        } finally {
            ctx.stop();
        }
    }

    @Override
    public boolean safeAdd(String key, Object value, int expiration) {
        init();
        final Timer.Context ctx = safeAddTimer.time();
        try {
            boolean result = underlyingCache.safeAdd(key, value, expiration);
            return result;
        } finally {
            ctx.stop();
        }
    }

    @Override
    public void set(String key, Object value, int expiration) {
        init();
        final Timer.Context ctx = setTimer.time();
        try {
            underlyingCache.set(key, value, expiration);
        } finally {
            ctx.stop();
        }
    }

    @Override
    public boolean safeSet(String key, Object value, int expiration) {
        init();
        final Timer.Context ctx = safeSetTimer.time();
        try {
            boolean result = underlyingCache.safeSet(key, value, expiration);
            return result;
        } finally {
            ctx.stop();
        }
    }

    @Override
    public void replace(String key, Object value, int expiration) {
        init();
        final Timer.Context ctx = replaceTimer.time();
        try {
            underlyingCache.replace(key, value, expiration);
        } finally {
            ctx.stop();
        }
    }

    @Override
    public boolean safeReplace(String key, Object value, int expiration) {
        init();
        final Timer.Context ctx = safeReplaceTimer.time();
        try {
            return underlyingCache.safeReplace(key, value, expiration);
        } finally {
            ctx.stop();
        }
    }

    @Override
    public Object get(String key) {
        init();
        final Timer.Context ctx = getTimer.time();
        try {
            Object result = underlyingCache.get(key);
            if (result == null) {
                missCounter.inc();
            } else {
                hitCounter.inc();
            }
            return result;
        } finally {
            ctx.stop();
        }
    }

    @Override
    public Map<String, Object> get(String[] keys) {
        init();
        final Timer.Context ctx = getManyTimer.time();
        try {
            Map<String, Object> result = underlyingCache.get(keys);
            if (result == null || result.isEmpty()) {
                missCounter.inc(keys.length);
            } else {
                hitCounter.inc(result.size());
                missCounter.inc(keys.length - result.size());
            }
            return result;
        } finally {
            ctx.stop();
        }
    }

    @Override
    public long incr(String key, int by) {
        init();
        final Timer.Context ctx = incrTimer.time();
        try {
            long result = underlyingCache.incr(key, by);
            return result;
        } finally {
            ctx.stop();
        }
    }

    @Override
    public long decr(String key, int by) {
        init();
        final Timer.Context ctx = decrTimer.time();
        try {
            long result = underlyingCache.decr(key, by);
            return result;
        } finally {
            ctx.stop();
        }
    }

    @Override
    public void clear() {
        init();
        final Timer.Context ctx = clearTimer.time();
        try {
            underlyingCache.clear();
        } finally {
            ctx.stop();
        }
    }

    @Override
    public void delete(String key) {
        init();
        final Timer.Context ctx = deleteTimer.time();
        try {
            underlyingCache.delete(key);
        } finally {
            ctx.stop();
        }
    }

    @Override
    public boolean safeDelete(String key) {
        init();
        final Timer.Context ctx = safeDeleteTimer.time();
        try {
            boolean result = underlyingCache.safeDelete(key);
            return result;
        } finally {
            ctx.stop();
        }
    }
}
