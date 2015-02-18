package ninja.template.directives;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import ninja.Context;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * @author svenkubiak
 *
 */
@SuppressWarnings("rawtypes")
public class TemplateEngineFreemarkerAuthenticityTokenDirective implements TemplateDirectiveModel {
    private String authenticityToken;

    public TemplateEngineFreemarkerAuthenticityTokenDirective(Context context) {
        this.authenticityToken = context.getSession().getAuthenticityToken();
    }
    
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        if (!params.isEmpty()) {
            throw new TemplateException("This directive doesn't allow parameters.", env);
        }
        
        if (loopVars.length != 0) {
            throw new TemplateException("This directive doesn't allow loop variables.", env);
        }
        
        Writer out = env.getOut();
        out.append(this.authenticityToken);
    }
}