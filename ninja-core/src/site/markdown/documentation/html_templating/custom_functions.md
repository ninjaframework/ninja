Custom Functions
===============================


Custom functions available in templates
---------------------------------
Ninja supports custom Freemarker functions to make it easy to support standard, reusable UI widgets across your application. Custom Freemarker functions are like special function calls to render components of your page, and they can come packaged with their own CSS and JavaScript.

You can add your own custom Freemarker function in <code>Module</code> using Java code:
<pre class="prettyprint">

public class Module extends AbstractModule {

	protected void configure() {
		MapBinder<String, TemplateModel> mapbinder
                = MapBinder.newMapBinder(binder(), String.class, TemplateModel.class);
		mapbinder.addBinding("upper").toInstance(
			new TemplateMethodModelEx() {
				@Override
				public Object exec(List args) throws TemplateModelException {
					return new SimpleScalar(args.get(0).toString().toUpperCase());
				}
			});
	}

}

</pre>

And in your template :
<pre class="prettyprint">
${upper('Make me upper case.')}
</pre>

Custom functions available get Context
---------------------------------
<code>TemplateEngineFreemarkerContextDirectiveModel</code> class supports get context.

For example, You can implementing custom XSRF token If you don't want to use <code><@authenticityToken/></code>. You can add your own custom directive model using Java code:
<pre class="prettyprint">
public class TemplateEngineFreemarkerXsrfTokenDirective extends TemplateEngineFreemarkerContextDirectiveModel {


    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        if (!params.isEmpty()) {
            throw new TemplateException("This directive doesn't allow parameters.", env);
        }

        if (loopVars.length != 0) {
            throw new TemplateException("This directive doesn't allow loop variables.", env);
        }

        Writer out = env.getOut();
        Context context = getContext();
        String xsrfToken = context.getAttribute(CookieConstant.XSRF, String.class);
        if (xsrfToken == null) {
            xsrfToken = UUID.randomUUID().toString();
            context.addCookie(Cookie.builder("_xsrf", xsrfToken)
                    .setHttpOnly(false).setMaxAge(86400).build());
            context.setAttribute("_xsrf", xsrfToken);
        }
        return xsrfToken;
    }
}
</pre>

Add your own <code>TemplateEngineFreemarkerXsrfTokenDirective</code> in <code>Module</code>, using Java code:
<pre class="prettyprint">

public class Module extends AbstractModule {

	protected void configure() {
		MapBinder<String, TemplateModel> mapbinder
                = MapBinder.newMapBinder(binder(), String.class, TemplateModel.class);
        mapbinder.addBinding("xsrfToken").to(TemplateEngineFreemarkerXsrfTokenDirective.class);
	}

}

</pre>

To get the XSRF token, use the following code
<code><@xsrfToken/></code>