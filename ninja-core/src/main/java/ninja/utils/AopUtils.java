package ninja.utils;

import java.lang.reflect.Proxy;

/**
 * AOP utility methods.
 */
public class AopUtils {

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * Check whether the given object is a proxy.
     *
     * @param object the object to check
     * @return {@code true} if the object is a proxy
     */
    public static boolean isAopProxy(final Object object) {
        return (object != null) &&
                (Proxy.isProxyClass(object.getClass()) || object.getClass().getName().contains(CGLIB_CLASS_SEPARATOR));
    }

    /**
     * Check whether the given class is a proxy.
     *
     * @param clazz the class to check
     * @return {@code true} if the class is a proxy
     */
    public static boolean isAopProxy(final Class<?> clazz) {
        return (clazz != null) &&
                (Proxy.isProxyClass(clazz) || clazz.getName().contains(CGLIB_CLASS_SEPARATOR));
    }
}
