package ninja.session;

public class SessionTimeImpl implements SessionTime {

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
