package ninja.template;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import ninja.Context;
import ninja.Result;
import ninja.i18n.Lang;
import ninja.utils.ResponseStreams;

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
	TemplateEngineFreemarker(Lang lang, Logger logger, TemplateEngineHelper templateEngineHelper, TemplateEngineManager templateEngineManager) {
		this.lang = lang;
		this.logger = logger;
		this.templateEngineHelper = templateEngineHelper;
		cfg = new Configuration();
		cfg.setClassForTemplateLoading(this.getClass(), "/");
		
		//we are going to enable html escaping by default using this template loader:
		cfg.setTemplateLoader(new TemplateEngineFreemarkerEscapedLoader(cfg.getTemplateLoader()));
	}
    


	@Override
	public void invoke(Context context, Result result) {

		Object object = result.getRenderable();
		
		ResponseStreams responseStreams = context.finalizeHeaders(result);
		
		Map map;
		//if the object is null we simply render an empty map...
		if (object == null) {
		    map = Maps.newHashMap();
		} else if (!(object instanceof Map)) {
			throw new RuntimeException(
					"Freemarker Templating engine can only render Map of Strings...");

		} else {
			map = (Map) object;
		}		
		
		// provide all i18n templates to freemarker engine:
		Locale locale = context.getHttpServletRequest().getLocale();		
		Map<Object, Object> i18nMap = lang.getAll(locale);		
		map.putAll(i18nMap);

		String templateName = templateEngineHelper.getTemplateForResult(context.getRoute(),
                result, FILE_SUFFIX);

		// Specify the data source where the template files come from.
		// Here I set a file directory for it:
		try {

			Template freemarkerTemplate = cfg.getTemplate(templateName);

			// convert tuples:

			freemarkerTemplate.process(map, responseStreams.getWriter());

			responseStreams.getWriter().flush();			
			responseStreams.getWriter().close();

		} catch (Exception e) {
			logger.error("Error processing Freemarker Template " + templateName, e);
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
