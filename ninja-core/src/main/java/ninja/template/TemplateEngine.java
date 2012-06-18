package ninja.template;

import ninja.Context;

public interface TemplateEngine {
	
	public void invoke(Context context, Object object);

}
