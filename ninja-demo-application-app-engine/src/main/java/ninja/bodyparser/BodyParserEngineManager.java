package ninja.bodyparser;

import com.google.inject.ImplementedBy;

@ImplementedBy(BodyParserEngineManagerImpl.class)
public interface BodyParserEngineManager {
		
	BodyParserEngine getBodyParserEngineForContentType(String contentType);

}
