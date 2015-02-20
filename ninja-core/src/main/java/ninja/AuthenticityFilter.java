package ninja;

import ninja.utils.NinjaConstant;

import com.google.inject.Inject;

/**
 * 
 * @author svenkubiak
 *
 */
public class AuthenticityFilter implements Filter {
    
    private NinjaDefault ninjaDefault;

    @Inject
    public AuthenticityFilter(NinjaDefault ninjaDefault) {
        this.ninjaDefault = ninjaDefault;
    }
    
    @Override
    public Result filter(FilterChain filterChain, Context context) {
        String authenticityToken = context.getParameter(NinjaConstant.AUTHENTICITY_TOKEN);
        if (!context.getSession().getAuthenticityToken().equals(authenticityToken)) {
            return ninjaDefault.getForbiddenResult(context);
        }
        
        return filterChain.next(context);
    }
}