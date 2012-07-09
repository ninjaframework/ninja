package ninja.lifecycle;

import java.lang.reflect.Method;

/**
 * A lifecycle target, ie something to start/stop
 *
 * @author James Roper
 */
class Target implements Comparable<Target> {
    private final Method startMethod;
    private final Object target;
    private final int order;

    Target(Method startMethod, Object target, int order) {
        this.startMethod = startMethod;
        this.target = target;
        this.order = order;
    }

    public Method getStartMethod() {
        return startMethod;
    }

    public Object getTarget() {
        return target;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(Target o) {
        return order - o.getOrder();
    }
}
