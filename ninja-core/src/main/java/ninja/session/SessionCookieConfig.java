package ninja.session;

public interface SessionCookieConfig {

    public static final String sessionExpireTimeInSeconds = "sessionExpireTimeInSeconds";
    public static final String sessionSendOnlyIfChanged = "sessionSendOnlyIfChanged";
    public static final String sessionTransferredOverHttpsOnly = "sessionTransferredOverHttpsOnly";
}
