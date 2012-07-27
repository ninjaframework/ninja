package ninja.bodyparser;

import java.io.IOException;

import ninja.Context;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class BodyParserEngineJson implements BodyParserEngine {

	public <T> T invoke(Context context, Class<T> classOfT) {

		Gson gson = new Gson();

		T t = null;

		try {

			t = gson.fromJson(context.getHttpServletRequest().getReader(),
			        classOfT);

		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t;
	}

}
