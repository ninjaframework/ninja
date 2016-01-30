package ninja.template;

import freemarker.template.TemplateDirectiveModel;
import ninja.Context;

public abstract class TemplateEngineFreemarkerContextDirectiveModel implements TemplateDirectiveModel {
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
    
}
