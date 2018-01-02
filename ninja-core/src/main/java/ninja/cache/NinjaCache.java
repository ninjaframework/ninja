/**
 * Copyright (C) 2012- the original author or authors.
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

package ninja.cache;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Map;

import ninja.utils.TimeUtil;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A convenience class to access the underlying cache implementation.
 * 
 * Makes getting and setting of objects a lot simpler.
 * 
 * This class originates from Play 1.2.5's excellent cache implementation.
 * 
 * @author ra
 *
 */
@Singleton
public class NinjaCache {
    
    final Cache cache;
    
    @Inject
    public NinjaCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * Add an element only if it doesn't exist.
     * @param key Element key
     * @param value Element value
     * @param expiration Ex: 10s, 3mn, 8h
     */
    public void add(String key, Object value, String expiration) {
        checkSerializable(value);
        cache.add(key, value, TimeUtil.parseDuration(expiration));
    }

    /**
     * Add an element only if it doesn't exist, and return only when 
     * the element is effectively cached.
     * @param key Element key
     * @param value Element value
     * @param expiration Ex: 10s, 3mn, 8h
     * @return If the element an eventually been cached
     */
    public boolean safeAdd(String key, Object value, String expiration) {
        checkSerializable(value);
        return cache.safeAdd(key, value, TimeUtil.parseDuration(expiration));
    }

    /**
     * Add an element only if it doesn't exist and store it indefinitely.
     * @param key Element key
     * @param value Element value
     */
    public void add(String key, Object value) {
        checkSerializable(value);
        cache.add(key, value, TimeUtil.parseDuration(null));
    }

    /**
     * Set an element.
     * @param key Element key
     * @param value Element value
     * @param expiration Ex: 10s, 3mn, 8h
     */
    public void set(String key, Object value, String expiration) {
        checkSerializable(value);
        cache.set(key, value, TimeUtil.parseDuration(expiration));
    }

    /**
     * Set an element and return only when the element is effectively cached.
     * @param key Element key
     * @param value Element value
     * @param expiration Ex: 10s, 3mn, 8h
     * @return If the element an eventually been cached
     */
    public boolean safeSet(String key, Object value, String expiration) {
        checkSerializable(value);
        return cache.safeSet(key, value, TimeUtil.parseDuration(expiration));
    }

    /**
     * Set an element and store it indefinitely.
     * @param key Element key
     * @param value Element value
     */
    public void set(String key, Object value) {
        checkSerializable(value);
        cache.set(key, value, TimeUtil.parseDuration(null));
    }

    /**
     * Replace an element only if it already exists.
     * @param key Element key
     * @param value Element value
     * @param expiration Ex: 10s, 3mn, 8h
     */
    public void replace(String key, Object value, String expiration) {
        checkSerializable(value);
        cache.replace(key, value, TimeUtil.parseDuration(expiration));
    }

    /**
     * Replace an element only if it already exists and return only when the 
     * element is effectively cached.
     * @param key Element key
     * @param value Element value
     * @param expiration Ex: 10s, 3mn, 8h
     * @return If the element an eventually been cached
     */
    public boolean safeReplace(String key, Object value, String expiration) {
        checkSerializable(value);
        return cache.safeReplace(key, value, TimeUtil.parseDuration(expiration));
    }

    /**
     * Replace an element only if it already exists and store it indefinitely.
     * @param key Element key
     * @param value Element value
     */
    public void replace(String key, Object value) {
        checkSerializable(value);
        cache.replace(key, value, TimeUtil.parseDuration(null));
    }

    /**
     * Increment the element value (must be a Number).
     * @param key Element key 
     * @param by The incr value
     * @return The new value
     */
    public long incr(String key, int by) {
        return cache.incr(key, by);
    }

    /**
     * Increment the element value (must be a Number) by 1.
     * @param key Element key 
     * @return The new value
     */
    public long incr(String key) {
        return cache.incr(key, 1);
    }

    /**
     * Decrement the element value (must be a Number).
     * @param key Element key 
     * @param by The decr value
     * @return The new value
     */
    public long decr(String key, int by) {
        return cache.decr(key, by);
    }

    /**
     * Decrement the element value (must be a Number) by 1.
     * @param key Element key 
     * @return The new value
     */
    public long decr(String key) {
        return cache.decr(key, 1);
    }

    /**
     * Retrieve an object.
     * @param key The element key
     * @return The element value or null
     */
    public Object get(String key) {
        return cache.get(key);
    }

    /**
     * Bulk retrieve.
     * @param key List of keys
     * @return Map of keys & values
     */
    public Map<String, Object> get(String... key) {
        return cache.get(key);
    }

    /**
     * Delete an element from the cache.
     * @param key The element key
     */
    public void delete(String key) {
        cache.delete(key);
    }

    /**
     * Delete an element from the cache and return only when the 
     * element is effectively removed.
     * @param key The element key
     * @return If the element an eventually been deleted
     */
    public boolean safeDelete(String key) {
        return cache.safeDelete(key);
    }

    /**
     * Clear all data from cache.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Convenient clazz to get a value a class type;
     * @param <T> The needed type
     * @param key The element key
     * @param clazz The type class
     * @return The element value or null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        return (T) cache.get(key);
    }
    
    
    /**
     * Utility that check that an object is serializable.
     */
    void checkSerializable(Object value) {
        if (value != null && !(value instanceof Serializable)) {
            throw new CacheException(
                    "Cannot cache a non-serializable value of type "
                            + value.getClass().getName(),
                    new NotSerializableException(value.getClass().getName()));
        }
    }

}
