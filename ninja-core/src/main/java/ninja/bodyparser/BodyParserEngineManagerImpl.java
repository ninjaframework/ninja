package ninja.bodyparser;

import ninja.ContentTypes;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEngineManagerImpl implements BodyParserEngineManager {

	private final BodyParserEngineJson bodyParserEngineJson;

	@Inject
	public BodyParserEngineManagerImpl(BodyParserEngineJson bodyParserEngineJson) {
		this.bodyParserEngineJson = bodyParserEngineJson;

	}

	@Override
	public BodyParserEngine getBodyParserEngineForContentType(String contentType) {

		if (contentType.equals(ContentTypes.APPLICATION_JSON)) {
			return bodyParserEngineJson;
		} else {
			return null;
		}

	}
}
