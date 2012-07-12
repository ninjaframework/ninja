package ninja.template;

import ninja.Context;
import ninja.Result;

public interface TemplateEngine {

    /**
     * Render the given object to the given context
     *
     * @param context The context to render to
     * @param result  The result to render
     */
    public void invoke(Context context, Result result);

    /**
     * Get the content type this template engine renders
     *
     * @return The content type this template engine renders
     */
    public String getContentType();

}
