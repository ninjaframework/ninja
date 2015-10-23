package ninja.session;

import com.google.inject.ImplementedBy;

@ImplementedBy(SessionTimeImpl.class)
public interface SessionTime {
    long currentTimeMillis();
}
