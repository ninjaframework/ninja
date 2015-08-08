Advanced topics - Security, i18n and more
=========================================

Security and templating
-----------------------

Ninja is HTML escaping all variables that you render by default. If you don't want to do that you
can use the "noescape" directive around that particular variable.
<pre class="prettyprint">
&lt;#noescape&gt;${yourVariableThatShouldNotBeEscaped}&lt;/#noescape&gt;
</pre>


i18n and the view
----------------

You can access all messages by using a simple method inside your templates.

Lets say your message.properties looks like:

<pre class="prettyprint">
casinoRegistrationTitle=My funky title
</pre>   

You can then access the variable inside your view like that.

<pre class="prettyprint">
&lt;html&gt;
    &lt;head&gt;
        &lt;title&gt;${i18n(&quot;casinoRegistrationTitle&quot;)}&lt;/title&gt;
    &lt;/head&gt;
&lt;html&gt;
</pre>

Flash content in the view
-------------------------

The flash scope of the application is important. It helps to maintain
a little bit of state between requests even on
a RESTful architecture.

Rendering the flash error and success messages is straight forward.

Please note that all variables of the flash scope are prefixed with
"flash.".

A simple demo of that

<pre class="prettyprint"> 
&lt;#if (flash.error)??&gt;
        &lt;p class=&quot;error&quot;&gt;${flash.error}&lt;/p&gt;
&lt;/#if&gt;

&lt;#if (flash.success)??&gt;
        &lt;p class=&quot;success&quot;&gt;${flash.success}&lt;/p&gt;
&lt;/#if&gt;
</pre>

This simply checks if a flash error or success is there and prints the message out.
The message itself is being translated.



Implicit variables available in templates
-----------------------------------------

 * <code>${session.*}</code> You can access all session-cookie values 
   by their keys prefixed with the accessor "session.". E.g.: 
   If you had set a cookie with the key "username", then you can use 
   <code>${session.username}</code> to resolve the username and display it.
 * <code>${flash.success}</code> Translated (if possible) flash success message 
   (via success("value")).
 * <code>${flash.error}</code> Translated (if possible) flash error message 
   (via error("value")).
 * <code>${flash.*}</code> Translated (if possible) flash message with 
   arbitrary key (via put("key", "value")).
 * <code>${lang}</code> resolves to the language Ninja uses currently. 
 * <code>${contextPath}</code> resolves the context path of the application 
   (empty if running on root)

Implicit functions available in templates
-----------------------------------------

### reverseRoute(...)

Reverse route allows you to calculate a reverse route inside your templates.
For instance via <code>${reverseRoute("controllers.ApplicationController", 
"userDashboard", "email", email, "id", id)}</code>.

First parameter is the controller name, second parameter the method name. All
other parameters are optional and used to replace variable parts inside the route with
appropriate values. 

In the example above the user rendered the variable parts
with <code>Results.html().render("id", 1000).render("email", "my@email.com") </code>.

For a route like 
<code>router.GET().route("/user/{id}/{email}/userDashboard").with(ApplicationController.class, "userDashboard");</code> 
the result is: <code>/me/user/1000/my@email.com/userDashboard</code>.


### assetsAt(...)

assetsAt is a shortcut to get a reverse route for an asset of your assets directory.
<code>${assetsAt("css/custom.css")}</code> would render
the location of custom.css. The corresponding route could be 
<code>router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");</code>.

This would then result in the following output: <code>/assets/css/custom.css</code>.


### webJarsAt(...)

webJarsAt allows you to render webjar contents. For instance
<code>${webJarsAt("bootstrap/3.3.4/css/bootstrap.min.css")}</code> would render
a css file from a webJars jar. The corresponding route could be 
<code>router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");</code>.

This would then result in the following output: <code>/assets/webjars/bootstrap/3.3.4/css/bootstrap.min.css</code>.

### i18n(...)

i18n allows you to render translated strings. For instance <code>${i18n(&quot;myi18nKey&quot;)}</code> allows
you to render the value for myi18nKey in the correct language for your user.
Please refer to chapter "internationalization" for more information.


### prettyTime(...)

prettyTime allows you to format localized relative dates.
<code>${prettyTime(myDate)}</code>

For instance, if you had a date object that represented yesterday, prettyTime would format that as *1 day ago* in the
preferred Locale of the request.

### authenticity(...)

authenticity allows you to retrieve an authenticity token for protection against CSRF-requests. You can either get a prefilled hidden input field or simply the token itself.

To get the prefilled hidden input field, use the following code
<code><@authenticityForm/></code>

To get the token, use the following code
<code><@authenticityToken/></code>

If you use either the form or the token you might want to check the token in your controller. Ninja offers a filter for checking the correctness of the token. Just add the following filter to your controller class or method.
<code>FilterWith(AuthenticityFilter.class)</code>

If the token is invalid the use will see a 403 Forbidden error page.

Advanced usage of Freemarker
----------------------------

Freemarker is the templating language we are using for rendering views. 
It can do a lot of cool stuff, and you should refer to http://freemarker.org/
to learn more.

Note that the Freemarker <code>Configuration</code> object can be accessed via your application <code>TemplateEngineFreemarker</code> singleton. According to the FreeMarker documentation, the configuration will be thread-safe once all settings have been set via a safe publication technique. Therefore, consider modifying it only within the <code>configure()</code> method of your application <code>Module</code> singleton.