package ninja.jackson;

import ninja.utils.ObjectMapperProvider;
import ninja.utils.XmlMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class JacksonModule extends AbstractModule {
	@Override
	protected void configure() {
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
        bind(XmlMapper.class).toProvider(XmlMapperProvider.class).in(Singleton.class);

		bind(TemplateEngineJson.class);
		bind(TemplateEngineJsonP.class);
		bind(TemplateEngineXml.class);

		bind(BodyParserEngineJson.class);
		bind(BodyParserEngineXml.class);
	}
}