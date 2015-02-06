package ninja.template;

import java.util.List;

import ninja.utils.NinjaConstant;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 
 * @author svenkubiak
 *
 */
public class TemplateEngineFreemarkerAuthenticityMethod implements TemplateMethodModelEx{
    private String authenticityToken;
    
    public TemplateEngineFreemarkerAuthenticityMethod(String authenticityToken) {
        this.authenticityToken = authenticityToken;
    }

    @Override
    public String exec(List args) throws TemplateModelException {
        String arg = (args != null && args.size() == 1) ? String.valueOf(args.get(0)) : null;
        
        if (("form").equalsIgnoreCase(arg)) {
            return "<input type='hidden' value='" + this.authenticityToken + " name='" + NinjaConstant.AUTHENTICITY_TOKEN +  "' />";
        } else if (("token").equalsIgnoreCase(arg)) {
            return this.authenticityToken;
        }
        
        return "";
    }
}