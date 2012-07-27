package ninja;

/**
 * A simple default implementation of a SecureFilter.
 * 
 * If you annotate your methods using that filter it will
 * check if a variable called "username" is saved in the cookie.
 * 
 * If yes it will continue the execution. If not it will break.
 * 
 * 
 * NinjaFilter are really simple. If this one does not suit your needs
 * modify it for your project :)
 * 
 * 
 * @author rbauer
 *
 */
public class SecureFilter implements Filter {

    /** If a username is saved we assume the session is valid */
    public final String USERNAME = "username";

    @Override
    public Result filter(FilterChain chain, Context context) {

        // if we got no cookies we break:
        if (context.getSessionCookie() == null
                || context.getSessionCookie().get(USERNAME) == null) {
            
        	
        	return Results.forbidden().html().template("/views/forbidden403.ftl.html");

        } else {
            return chain.next(context);
        }

    }
}
