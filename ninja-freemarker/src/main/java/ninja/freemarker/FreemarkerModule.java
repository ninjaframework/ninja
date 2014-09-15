package ninja.freemarker;

import com.google.inject.AbstractModule;

public class FreemarkerModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(TemplateEngineFreemarker.class);
	}
}