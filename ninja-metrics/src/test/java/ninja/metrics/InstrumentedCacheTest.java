/**
 * Copyright (C) 2012-2015 the original author or authors.
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

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import ninja.cache.Cache;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.getInternalState;

public class InstrumentedCacheTest {

    private Cache cache;

    private MetricsService metricsService;

    private MetricRegistry metricRegistry;

    private InstrumentedCache instrumentedCache;

    private static final long INITIAL_HIT_COUNT = 10;

    private static final long INITIAL_MISS_COUNT = 5;

    @Before
    public void setup() {
        cache = mock(Cache.class);
        metricsService = mock(MetricsService.class);
        metricRegistry = mock(MetricRegistry.class);
        when(metricsService.getMetricRegistry()).thenReturn(metricRegistry);
        when(metricRegistry.counter("ninja.cache.hits")).thenReturn(getCounter(INITIAL_HIT_COUNT));
        when(metricRegistry.counter("ninja.cache.miss")).thenReturn(getCounter(INITIAL_MISS_COUNT));
        instrumentedCache = new InstrumentedCache(cache, metricsService);
    }

    @Test
    public void testAdd() {
        instrumentedCache.add("myKey", "myValue", 10);
        verify(cache).add("myKey", "myValue", 10);
    }

    @Test
    public void testSafeAdd() {
        instrumentedCache.safeAdd("myKey", "myValue", 10);
        verify(cache).safeAdd("myKey", "myValue", 10);
    }

    @Test
    public void testSet() {
        instrumentedCache.set("myKey", "myValue", 10);
        verify(cache).set("myKey", "myValue", 10);
    }

    @Test
    public void testSafeSet() {
        instrumentedCache.safeSet("myKey", "myValue", 10);
        verify(cache).safeSet("myKey", "myValue", 10);
    }

    @Test
    public void testReplace() {
        instrumentedCache.replace("myKey", "myValue", 10);
        verify(cache).replace("myKey", "myValue", 10);
    }

    @Test
    public void testSafeReplace() {
        instrumentedCache.safeReplace("myKey", "myValue", 10);
        verify(cache).safeReplace("myKey", "myValue", 10);
    }

    @Test
    public void testGet_ForHit_withSingleValue() throws NoSuchFieldException, IllegalAccessException {
        when(cache.get("myKey")).thenReturn("A string value");

        Object result = instrumentedCache.get("myKey");

        assertEquals("A string value", result);
        assertEquals("hit counter must increase by 1", INITIAL_HIT_COUNT + 1, getHitCount());
        assertEquals("miss counter must remain same", INITIAL_MISS_COUNT, getMissCount());
    }

    @Test
    public void testGet_ForMiss_withSingleValue() throws NoSuchFieldException, IllegalAccessException {
        when(cache.get("myKey")).thenReturn(null);

        Object result = instrumentedCache.get("myKey");

        assertEquals(null, result);
        assertEquals("miss counter must increase", INITIAL_MISS_COUNT + 1, getMissCount());
        assertEquals("hit counter must remain same", INITIAL_HIT_COUNT, getHitCount());
    }

    @Test
    public void testGet_ForHit_withMultipleValues() throws NoSuchFieldException, IllegalAccessException {
        String[] keysForGetting = new String[]{"key1", "key2"};
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "val1");
        map.put("key2", "val2");

        when(cache.get(keysForGetting)).thenReturn(map);

        Map<String, Object> result = instrumentedCache.get(keysForGetting);

        assertEquals("val1", result.get("key1"));
        assertEquals("val2", result.get("key2"));

        assertEquals("hit counter must increase by 2", INITIAL_HIT_COUNT + 2, getHitCount());
        assertEquals("miss counter must remain same", INITIAL_MISS_COUNT, getMissCount());
    }

    @Test
    public void testGet_ForMiss_withMultipleValues() throws NoSuchFieldException, IllegalAccessException {
        String[] keysForGetting = new String[]{"key1", "key2"};

        when(cache.get(keysForGetting)).thenReturn(null);

        Map<String, Object> result = instrumentedCache.get(keysForGetting);

        assertNull("Null must be returned as per our mocking", result);
        assertEquals("hit counter must remains same", INITIAL_HIT_COUNT, getHitCount());
        assertEquals("miss counter must increase by 2", INITIAL_MISS_COUNT + 2, getMissCount());
    }

    @Test
    public void testIncr() {
        instrumentedCache.incr("myKey", 2);
        verify(cache).incr("myKey", 2);
    }

    @Test
    public void testDecr() {
        instrumentedCache.decr("myKey", 2);
        verify(cache).decr("myKey", 2);
    }

    @Test
    public void testClear() {
        instrumentedCache.clear();
        verify(cache).clear();
    }

    @Test
    public void testDelete() {
        instrumentedCache.delete("myKey");
        verify(cache).delete("myKey");
    }

    @Test
    public void testSafeDelete() {
        instrumentedCache.safeDelete("myKey");
        verify(cache).safeDelete("myKey");
    }

    private Counter getCounter(long value) {
        Counter counter = new Counter();
        counter.inc(value);
        return counter;
    }

    private long getMissCount() throws NoSuchFieldException, IllegalAccessException {
        Counter missCounter = (Counter) getInternalState(instrumentedCache, "missCounter");
        return missCounter.getCount();
    }

    private long getHitCount() throws NoSuchFieldException, IllegalAccessException {
        Counter hitCounter = (Counter) getInternalState(instrumentedCache, "hitCounter");
        return hitCounter.getCount();
    }

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {

        when(metricRegistry.counter("ninja.cache.hits")).thenReturn(getCounter(12));
        when(metricRegistry.counter("ninja.cache.miss")).thenReturn(getCounter(5));

        when(metricsService.getMetricRegistry()).thenReturn(metricRegistry);
        InstrumentedCache instrumentedCache = new InstrumentedCache(cache, metricsService);

        assertEquals("cache must be set as a private field in the instrumentedCache",
                cache, getInternalState(instrumentedCache, "underlyingCache"));

        assertEquals("metricService must be set as a private field in the instrumentedCache",
                metricsService, getInternalState(instrumentedCache, "metricsService"));

        Counter hitCounter = (Counter) getInternalState(instrumentedCache, "hitCounter");
        assertEquals("hit counter must be 12", 12, hitCounter.getCount());

        Counter missCounter = (Counter) getInternalState(instrumentedCache, "missCounter");
        assertEquals("miss counter must be 5", 5, missCounter.getCount());
    }
}
