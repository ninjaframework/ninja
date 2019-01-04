/**
 * Copyright (C) 2012-2019 the original author or authors.
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

import java.util.Map;

/**
 * Interface hiding cache implementation.
 * 
 * Inject that interface into the methods where you want to use it and you are
 * ready to go.
 * 
 * <code>@Inject Cache cache</code>.
 * 
 * Heavily inspired by excellent Play! 1.2.5 implementation.
 * 
 */
public interface Cache {

    /**
     * Add object to cache.
     * 
     * Important: If the key exists already it won't be overwritten. 
     * Use set(...) instead.
     * 
     * This method is fire and forget. Implementations may implement
     * this async and non-blocking.
     * 
     * @param key The key of the object to use for caching.
     * @param value The value of the object to add to the cache.
     * @param expirationInSeconds Expiration time in seconds.
     */
    public void add(String key, Object value, int expirationInSeconds);

    /**
     * Similar to {@link #add(key, value, expirationInSeconds) delete} method.
     * 
     * BUT it blocks until execution succeeds / fails AND wraps
     * exceptions in a simple true / false return value
     * 
     * @param key The key of the object to use for caching.
     * @param value The value of the object to add in the cache.
     * @param expirationInSeconds  Expiration time in seconds.
     * @return true if add was successful, false if not.
     */
    public boolean safeAdd(String key, Object value, int expirationInSeconds);

    /**
     * Adds object of cache.
     * 
     * Important: This method potentially overwrites previously existing
     * object with new values.
     * 
     * This method is fire and forget. Implementations may implement
     * this async and non-blocking.
     * 
     * @param key The key of the object to use for caching.
     * @param value The value of the object to set in the cache.
     * @param expirationInSeconds Expiration time in seconds.
     */
    public void set(String key, Object value, int expirationInSeconds);

    /**
     * Similar to {@link #set(key, value, expirationInSeconds) delete} method.
     * 
     * BUT it blocks until execution succeeds / fails AND wraps
     * exceptions in a simple true / false return value
     * 
     * @param key The key of the object to use for caching.
     * @param value The value of the object to set in the cache.
     * @param expirationInSeconds  Expiration time in seconds.
     * @return true if set was successful, false if not.
     */
    public boolean safeSet(String key, Object value, int expirationInSeconds);

    /**
     * Replaces key with new value. 
     * 
     * Important: Will do nothing if the key does not exist.
     * 
     * This method is fire and forget. Implementations may implement
     * this async and non-blocking.
     * 
     * @param key The key of the object to use for caching.
     * @param value The value of the object to replace in the cache.
     * @param expirationInSeconds  Expiration time in seconds.
     */
    public void replace(String key, Object value, int expirationInSeconds);

    /**
     * Similar to {@link #replace(key, value, expirationInSeconds) delete} method.
     * 
     * BUT it blocks until execution succeeds / fails AND wraps
     * exceptions in a simple true / false return value
     * 
     * @param key The key of the object to use for caching.
     * @param value The value of the object to replace in the cache.
     * @param expirationInSeconds  Expiration time in seconds.
     * @return true if replace was successful, false if not.
     */
    public boolean safeReplace(String key, Object value, int expirationInSeconds);

    /**
     * Returns the object for this key or null if not found.
     * 
     * @param key The key of the object to retrieve.
     * @return The object of the key or null if not found.
     */
    public Object get(String key);

    /**
     * Returns all objects for the keys.
     * 
     * @param keys The list of keys to retrieve from Cache.
     * @return A map with key - object pairs that were found in cache.
     */
    public Map<String, Object> get(String[] keys);

    /**
     * Increments key by value.
     * 
     * @param key The key to increment
     * @param by Value by which to increment.
     * @return New value of the key or -1 if key does not exist.
     */
    public long incr(String key, int by);

    /**
     * Decrements key by value.
     * 
     * @param key The key to decrement
     * @param by Value by which to decrement.
     * @return New value of the key or -1 if key does not exist.
     */
    public long decr(String key, int by);

    /**
     * Clear all values in cache.
     */
    public void clear();

    /**
     * Delete key from cache.
     * @param key The key to delete.
     */
    public void delete(String key);
    
    /**
     * Similar to {@link #delete(key) delete} method.
     * 
     * BUT it blocks until execution succeeds / fails AND wraps
     * exceptions in a simple true / false return value
     * 
     * @param key The key to delete
     * @return true if deletion was successful, false if not.
     */
    public boolean safeDelete(String key);

}
