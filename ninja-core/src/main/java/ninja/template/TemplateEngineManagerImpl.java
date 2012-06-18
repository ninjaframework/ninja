package ninja.template;

import ninja.ContentTypes;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TemplateEngineManagerImpl implements TemplateEngineManager {

	private final TemplateEngineFreemarker templateEngineFreemarker;
	private final TemplateEngineJsonGson templateEngineJsonGson;

	@Inject
	public TemplateEngineManagerImpl(TemplateEngineFreemarker templateEngineFreemarker,
	                                 TemplateEngineJsonGson templateEngineJsonGson) {

		this.templateEngineFreemarker = templateEngineFreemarker;
		this.templateEngineJsonGson = templateEngineJsonGson;

	}

	@Override
	public TemplateEngine getTemplateEngineForContentType(String contentType) {

		if (contentType.equals(ContentTypes.TEXT_HTML)) {
			return templateEngineFreemarker;
		} else if(contentType.equals(ContentTypes.APPLICATION_JSON)) {
			return templateEngineJsonGson;
		} else return null;

	}
}
