package ninja.template;

import ninja.Context;

public interface TemplateEngine {
	
	public void invoke(Context context, Object object);
	
	/**
	 * For instance returns ".ftl.html"
	 * Or .ftl.json.
	 * 
	 * Or anything else. To display error messages in a nice way...
	 * 
	 * But Gson for instance does not use a template to render stuff.
	 * Therefore it will return null
	 * 
	 * @return name of suffix or null if engine is not using a template on disk.
	 * 
	 */
	public String getSuffixOfTemplatingEngine();

}
