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

		if (ContentTypes.TEXT_HTML.equals(contentType)) {
			return templateEngineFreemarker;
		} else if (ContentTypes.APPLICATION_JSON.equals(contentType)) {
			return templateEngineJsonGson;
		} else return null;

	}
}
