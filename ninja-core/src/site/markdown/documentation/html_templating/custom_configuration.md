Custom Freemarker configuration
===============================

Advanced usage of Freemarker
----------------------------

Freemarker is the templating language we are using for rendering views. 
It can do a lot of cool stuff, and you should refer to http://freemarker.org/
to learn more.

The Freemarker <code>Configuration</code> object can be accessed via your application <code>TemplateEngineFreemarker</code> singleton. According to the FreeMarker documentation, the configuration will be thread-safe once all settings have been set via a safe publication technique. Therefore, consider modifying it only within an extended <code>NinjaDefault</code> class, during the framework start.

<pre class="prettyprint">
package conf;

public class Ninja extends NinjaDefault {

	@Inject
	protected TemplateEngineFreemarker templateEngineFreemarker;

	@Override
	public void onFrameworkStart() {
		super.onFrameworkStart();

		Configuration freemarkerConfiguration = templateEngineFreemarker.getConfiguration();
		...
	}

}
</pre>

Custom functions - The Java way
---------------------------------

While you can access the Freemarker <code>Configuration</code> object, you can add you own custom functions using Java code:
<pre class="prettyprint">
freemarkerConfiguration.setSharedVariable("upper", new TemplateMethodModelEx() {

	@Override
	public Object exec(List args) throws TemplateModelException {
		return new SimpleScalar(args.get(0).toString().toUpperCase());
	}
});
</pre>
And in your template :
<pre class="prettyprint">
${upper('Make me upper case.')}
</pre>

Custom functions - The Ftl way
------------------------------

But for templating purpose, you can also do it by defining sets of <code>macro</code> functions in separate .ftl.html files.
<pre class="prettyprint">
<#macro bold>
	<b><#nested /></b>
</#macro>
</pre>
And in your template :
<pre class="prettyprint">
<#import "../layout/functions.ftl.html" as f> 
<@f.bold>Make me bold</@f.bold>
</pre>
This last way should help you making reusable macros when using one or another HTML/CSS framework.
