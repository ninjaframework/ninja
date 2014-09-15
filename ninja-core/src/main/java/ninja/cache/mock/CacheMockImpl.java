package ninja.cache.mock;

import java.util.Map;

import ninja.cache.Cache;

public class CacheMockImpl implements Cache {

    @Override
    public void add(String key, Object value, int expiration) {
    }

    @Override
    public boolean safeAdd(String key, Object value, int expiration) {
        return false;
    }

    @Override
    public void set(String key, Object value, int expiration) {
    }

    @Override
    public boolean safeSet(String key, Object value, int expiration) {
        return false;
    }

    @Override
    public void replace(String key, Object value, int expiration) {
    }

    @Override
    public boolean safeReplace(String key, Object value, int expiration) {
        return false;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public Map<String, Object> get(String[] keys) {
        return null;
    }

    @Override
    public long incr(String key, int by) {
        return 0;
    }

    @Override
    public long decr(String key, int by) {
        return 0;
    }

    @Override
    public void clear() {
    }

    @Override
    public void delete(String key) {
    }

    @Override
    public boolean safeDelete(String key) {
        return false;
    }

}
