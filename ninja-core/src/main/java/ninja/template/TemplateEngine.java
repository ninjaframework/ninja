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
     * For instance returns ".ftl.html"
     * Or .ftl.json.
     * <p/>
     * Or anything else. To display error messages in a nice way...
     * <p/>
     * But Gson for instance does not use a template to render stuff.
     * Therefore it will return null
     *
     * @return name of suffix or null if engine is not using a template on disk.
     */
    public String getSuffixOfTemplatingEngine();

    /**
     * Get the content type this template engine renders
     *
     * @return The content type this template engine renders
     */
    public String getContentType();

}
