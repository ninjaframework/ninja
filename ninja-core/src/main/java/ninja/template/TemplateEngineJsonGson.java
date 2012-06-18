package ninja.template;

import java.io.IOException;

import ninja.Context;

import com.google.gson.Gson;

public class TemplateEngineJsonGson implements TemplateEngine {

	@Override
    public void invoke(Context context, Object object) {

		Gson gson = new Gson();
		String json = gson.toJson(object);
		
		try {
			context.getHttpServletResponse().getOutputStream().print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
    }

}
