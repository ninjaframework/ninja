package ninja.template;

import java.io.IOException;

import ninja.Context;

import com.google.gson.Gson;
import ninja.Result;

public class TemplateEngineJsonGson implements TemplateEngine {

	@Override
    public void invoke(Context context, Result result) {

		Gson gson = new Gson();
		String json = gson.toJson(result.getRenderable());
		
		try {
			context.getWriter().write(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
    }

    @Override
    public String getContentType() {
        return "application/json";
    }
}
