package ninja.jetbrick.template;


import ninja.Result;
import ninja.Route;
import ninja.template.TemplateEngineHelper;

public class JetTemplateEngineHelper extends TemplateEngineHelper {

    @Override
    public String getTemplateForResult(Route route, Result result, String suffix) {
        return "src/main/java" + super.getTemplateForResult(route, result, suffix);
        //.replace(".ftl.html", ".html");
    }
}
