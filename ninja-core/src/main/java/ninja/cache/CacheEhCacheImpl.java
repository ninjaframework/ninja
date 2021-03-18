/**
 * Copyright (C) the original author or authors.
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

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import ninja.lifecycle.Dispose;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * EhCache implementation.
 *
 * <p>Ehcache is an open source, standards-based cache used to boost performance,
 * offload the database and simplify scalability. Ehcache is robust, proven and
 * full-featured and this has made it the most widely-used Java-based cache.</p>
 *
 * See https://www.ehcache.org/
 *
 * expiration is specified in seconds
 * 
 * Heavily inspired by excellent Play! 1.2.5 implementation.
 * 
 */
@Singleton
public class CacheEhCacheImpl implements Cache {

    private final CacheManager ehCacheManager;

    private final net.sf.ehcache.Cache ehCache;

    private final String cacheName = "ninja";

    private final Logger logger;

    @Inject
    public CacheEhCacheImpl(Logger logger) {
        this.logger = logger;
        this.ehCacheManager = CacheManager.create();
        this.ehCacheManager.addCacheIfAbsent(cacheName);
        this.ehCache = ehCacheManager.getCache(cacheName);
    }

    public void add(String key, Object value, int expiration) {
        if (ehCache.get(key) != null) {
            return;
        }
        Element element = new Element(key, value);
        element.setTimeToLive(expiration);
        ehCache.put(element);
    }

    public void clear() {
        ehCache.removeAll();
    }

    public synchronized long decr(String key, int by) {
        Element e = ehCache.get(key);
        if (e == null) {
            return -1;
        }
        long newValue = ((Number) e.getObjectValue()).longValue() - by;
        Element newE = new Element(key, newValue);
        newE.setTimeToLive(e.getTimeToLive());
        ehCache.put(newE);
        return newValue;
    }

    public void delete(String key) {
        ehCache.remove(key);
    }

    public Object get(String key) {
        Element e = ehCache.get(key);
        return (e == null) ? null : e.getObjectValue();
    }

    public Map<String, Object> get(String[] keys) {
        Map<String, Object> result = new HashMap<String, Object>(keys.length);
        for (String key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    public synchronized long incr(String key, int by) {
        Element e = ehCache.get(key);
        if (e == null) {
            return -1;
        }
        long newValue = ((Number) e.getObjectValue()).longValue() + by;
        Element newE = new Element(key, newValue);
        newE.setTimeToLive(e.getTimeToLive());
        ehCache.put(newE);
        return newValue;

    }

    public void replace(String key, Object value, int expiration) {
        if (ehCache.get(key) == null) {
            return;
        }
        Element element = new Element(key, value);
        element.setTimeToLive(expiration);
        ehCache.put(element);
    }

    public boolean safeAdd(String key, Object value, int expiration) {
        try {
            add(key, value, expiration);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean safeDelete(String key) {
        try {
            delete(key);
            return true;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return false;
        }
    }

    public boolean safeReplace(String key, Object value, int expiration) {
        try {
            replace(key, value, expiration);
            return true;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return false;
        }
    }

    public boolean safeSet(String key, Object value, int expiration) {
        try {
            set(key, value, expiration);
            return true;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return false;
        }
    }

    public void set(String key, Object value, int expiration) {
        Element element = new Element(key, value);
        element.setTimeToLive(expiration);
        ehCache.put(element);
    }

    @Dispose
    public void stop() {
        if (ehCacheManager != null) {
            ehCacheManager.shutdown();            
        }
        
    }
}