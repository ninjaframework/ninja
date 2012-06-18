package ninja.template;

import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;

@ImplementedBy(TemplateEngineManagerImpl.class)
public interface TemplateEngineManager {
	
	TemplateEngine getTemplateEngineForContentType(String contentType);

}
