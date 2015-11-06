package ninja.utils;

import com.google.inject.ImplementedBy;

@ImplementedBy(ClockImpl.class)
public interface Clock {
    long currentTimeMillis();
}
