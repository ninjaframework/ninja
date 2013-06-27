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

    public void add(String key, Object value, int expiration);

    public boolean safeAdd(String key, Object value, int expiration);

    public void set(String key, Object value, int expiration);

    public boolean safeSet(String key, Object value, int expiration);

    public void replace(String key, Object value, int expiration);

    public boolean safeReplace(String key, Object value, int expiration);

    public Object get(String key);

    public Map<String, Object> get(String[] keys);

    public long incr(String key, int by);

    public long decr(String key, int by);

    public void clear();

    public void delete(String key);

    public boolean safeDelete(String key);

}
