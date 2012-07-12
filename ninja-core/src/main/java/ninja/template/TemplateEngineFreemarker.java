package ninja.template;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ninja.Context;
import ninja.i18n.Lang;
import ninja.utils.NoEscapeString;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.utility.HtmlEscape;

public class TemplateEngineFreemarker implements TemplateEngine {

	private String FILE_SUFFIX = ".ftl.html";

	private Configuration cfg;

	private final Lang lang;

	@Inject
	TemplateEngineFreemarker(Lang lang) {
		this.lang = lang;
		cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "/");

	}

	@Override
	public void invoke(Context context, Object object) {

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
		// they will be usually prefixed by a "i18n."
		// therefore your have to call ${i18n.mytext}
		Locale locale = context.getHttpServletRequest().getLocale();		
		Map<String, String> i18nMap = lang.getAll(locale);		
		map.putAll(i18nMap);

		
		//escape the mapped strings.


		String templateName = context.getTemplateName(FILE_SUFFIX);


		
		// Specify the data source where the template files come from.
		// Here I set a file directory for it:
		try {

			Template freemarkerTemplate = cfg.getTemplate(templateName);

			// convert tuples:

			freemarkerTemplate.process(map, context.getWriter());

			context.getWriter().flush();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String getSuffixOfTemplatingEngine() {
		return FILE_SUFFIX;

	}

    @Override
    public String getContentType() {
        return "text/html";
    }
    
    private Map escpaeStringsInMap(Map<String, String> map) {
    	
    	Map<String, String> returnMap = Maps.newHashMap();
    	
    	for (Entry<String, String> entry : map.entrySet()) {
    		
    		//String value = entry.getValue();
    		//if (value instanceof NoEscapeString) {
    			returnMap.put(entry.getKey(), entry.getValue());
    		//} else {
    			//returnMap.put(entry.getKey(), HtmlEscapersentry.getValue());
    			
    		//}
    		
    	}
    	
    	return map;
    	
    }
}
