package ninja.template;

import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;

/**
 * Template engine manager.  Has a number of built in template engines, and allows registering custom template
 * engines by registering explicit bindings of things that implement TemplateEngine.
 */
@ImplementedBy(TemplateEngineManagerImpl.class)
public interface TemplateEngineManager {

    /**
     * Find the template engine for the given content type
     *
     * @param contentType The content type
     * @return The template engine, if found
     */
	TemplateEngine getTemplateEngineForContentType(String contentType);

}
