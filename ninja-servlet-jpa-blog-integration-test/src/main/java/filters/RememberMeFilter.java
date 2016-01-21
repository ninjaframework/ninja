package filters;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.session.Session;

public class RememberMeFilter implements Filter {
    public Result filter(FilterChain chain, Context context) {
        Result result = chain.next(context);

        Session session = context.getSession();

        // Only extend if we previously saved the value 'rememberMe' to the session
        if (session.get("rememberMe") != null) {
            // Set the expiry time 30 days (in milliseconds) in the future
            session.setExpiryTime(30 * 24 * 60 * 60 * 1000L);
        }

        return result;
    }
}