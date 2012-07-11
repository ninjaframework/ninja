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
			context.getWriter().write(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
    }

	@Override
	public String getSuffixOfTemplatingEngine() {
		//does not use disk based templates...
		return null;
	}

    @Override
    public String getContentType() {
        return "application/json";
    }
}
