package yoda.threads;

import com.google.inject.Inject;
import ninja.Ninja;
import ninja.exceptions.BadRequestException;
import ninja.exceptions.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yoda.YodaAsyncTask;


/**
 * This can be used as an uncaught exception handler which will log an uncaught exception as a severe log message.
 * 
 * @author dhudson - created 16 Jun 2014
 * @since 1.0
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionHandler.class);  

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        StringBuilder sb = new StringBuilder(100);
        sb.append("Uncaught Exception");
        if (t != null) {
            sb.append(" in thread " + t.getName());
        }

        logger.error(sb.toString(), e);
    }

}
