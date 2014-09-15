package ninja.cache;

import java.io.NotSerializableException;

public class CacheException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CacheException(String message, NotSerializableException cause) {
        super(message, cause);
    }
}
