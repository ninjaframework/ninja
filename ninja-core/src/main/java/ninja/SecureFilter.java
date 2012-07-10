package ninja;

import ninja.Context.HTTP_STATUS;

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

    /** 
     * A simple variable that will be returned when the filter is asked if
     * it breaks the filter chain.
     */
    boolean continueExecution = true;
    
    /** If a username is saved we assume the session is valid */
    public final String USERNAME = "username";

    @Override
    public void filter(Context context) {

        // if we got no cookies we break:
        if (context.getSessionCookie() == null
                || context.getSessionCookie().get(USERNAME) == null) {
            
            continueExecution = false;           
            
            context.status(HTTP_STATUS.forbidden403).template("/views/forbidden403.ftl.html")
                    .renderHtml();

        } else {

            // continue... everything is fine :)
            continueExecution = true;
        }

    }

    @Override
    public boolean continueExecution() {
        return continueExecution;
    }

}
