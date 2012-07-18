package ninja.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class LoggerProvider implements Provider<Logger> {
    private final Logger applicationWideLogger;

    @Inject
    public LoggerProvider() {
        this.applicationWideLogger = LoggerFactory.getLogger("Ninja");
    }

    public Logger get() {
        return applicationWideLogger;
    }
}