package ninja.utils;

public class ClockImpl implements Clock {

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
