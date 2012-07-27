package ninja.bodyparser;

import ninja.Context;

public interface BodyParserEngine {

	<T> T invoke(Context context, Class<T> classOfT);

}
