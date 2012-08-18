package ninja.template;

import java.util.Map;
import java.util.Map.Entry;

import ninja.Context;
import ninja.Result;
import ninja.i18n.Lang;
import ninja.session.FlashCookie;
import ninja.utils.ResponseStreams;

import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateEngineFreemarker implements TemplateEngine {

    private String FILE_SUFFIX = ".ftl.html";

    private Configuration cfg;

    private final Lang lang;

    private final TemplateEngineHelper templateEngineHelper;

    private final Logger logger;

    @Inject
    TemplateEngineFreemarker(Lang lang,
                             Logger logger,
                             TemplateEngineHelper templateEngineHelper,
                             TemplateEngineManager templateEngineManager) {
        this.lang = lang;
        this.logger = logger;
        this.templateEngineHelper = templateEngineHelper;
        cfg = new Configuration();

        cfg.setClassForTemplateLoading(this.getClass(), "/");

        // we are going to enable html escaping by default using this template
        // loader:
        cfg.setTemplateLoader(new TemplateEngineFreemarkerEscapedLoader(cfg
                .getTemplateLoader()));
    }

    @Override
    public void invoke(Context context, Result result) {

        Object object = result.getRenderable();

        ResponseStreams responseStreams = context.finalizeHeaders(result);

        Map map;
        // if the object is null we simply render an empty map...
        if (object == null) {
            map = Maps.newHashMap();
        } else if (!(object instanceof Map)) {
            throw new RuntimeException(
                    "Freemarker Templating engine can only render Map of Strings...");

        } else {
            map = (Map) object;
        }

        // provide all i18n templates to freemarker engine:
        String language = context.getAcceptLanguage();
        Map<Object, Object> i18nMap = lang.getAll(language);
        map.putAll(i18nMap);
        
        // get contentOfFlashCookie
        // prefix keys with "flash_"
        for (Entry<String, String> entry : context.getFlashCookie().getCurrentFlashCookieData().entrySet()) {
            
            String messageValue;
            
            //if it is a translated message get it from the language
            if (entry.getValue().startsWith("i18n")) {
                
                messageValue = lang.get(entry.getValue(), language);
                
                if (messageValue == null) {
                    throw new RuntimeException("No translated message found for flash message key: " + entry.getValue());
                }
                
                //else it is something else (for form parameters for instance)
                // we don't touch it...
            } else {
                messageValue = entry.getValue();
            }
            

            
            map.put("flash_" + entry.getKey(), messageValue);           
        }


        String templateName = templateEngineHelper.getTemplateForResult(
                context.getRoute(), result, FILE_SUFFIX);

        // Specify the data source where the template files come from.
        // Here I set a file directory for it:
        try {

            Template freemarkerTemplate = cfg.getTemplate(templateName);

            // convert tuples:

            freemarkerTemplate.process(map, responseStreams.getWriter());

            responseStreams.getWriter().flush();
            responseStreams.getWriter().close();

        } catch (Exception e) {
            logger.error(
                    "Error processing Freemarker Template " + templateName, e);
        }

    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        return FILE_SUFFIX;
    }
}
