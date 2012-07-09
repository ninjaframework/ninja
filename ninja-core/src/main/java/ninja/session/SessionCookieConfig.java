package ninja.session;

public interface SessionCookieConfig {

    String sessionExpireTimeInMs = "sessionExpireTimeInMs";
    String sessionSendOnlyIfChanged = "sessionSendOnlyIfChanged";
    String sessionTransferredOverHttpsOnly = "sessionTransferredOverHttpsOnly"; 
}
