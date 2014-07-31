Upgrade guide
=============

Sometimes (and hopefully not too often) we have to introduce breaking changes
into Ninja's behavior. This document describes which steps are needed to upgrade
your application to the latest Ninja version. Simply start with your current 
version and then work your way up to the top of the document.


to latest
---------

to 3.3.0
--------

### Vastly improved conf.Ninja

Results you now generate can be customized and are usging content-negotiation.
A json request will get a json error message. Html request will get an html
error message. Please have a look at interface ninja.Ninja and ninja.NinjaDefault.
They show how the improved approach works.

to 3.2.0
---------

### i18n handling in templates has changed.

Let's say you used $i18n{"my.message.key"} and your messages.properties was missing a
value for key "my.message.key". Before that version the template would not
render but throw an exception instead. From now on this behavior is relaxed
and the key itself will be rendered instead (+ a logged error message). 
In the case above you'll find "my.message.key" inside the rendered template.

to 3.0.0
--------

### Session and Flash scopes

<code>SessionCookie</code> and <code>FlashCookie</code> changed their names. 
<code>SessionCookie</code> is now called
<code>Session</code> and <code>FlashCookie</code> is called <code>FlashScope</code>.

<code>Context</code> object reflects this by providing <code>getSession()</code> 
and <code>getFlashScope()</code> methods.

### Changes in serving of static assets

AssetsController's serve method has been deprecated for good. The replacement
are AssetsController's serveStatic and serveWebJars methods.

OLD:

<pre class="prettyprint">
router.GET().route("/assets/.*").with(AssetsController.class, "serve");
</pre>

NEW (direct replacement): 

<pre class="prettyprint">
router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
</pre>
 
This also means that you can now serve static assets from arbitrary directories or
even files from root like:
<pre class="prettyprint">
router.GET().route("/robots.txt").with(AssetsController.class, "serveStatic");
</pre>

to 2.4.0
--------

 * Ninja now uses "ninja.mode=prod" by default (and no longer dev). 
   Many users complained that it is quite strange to configure the prod 
   mode when running Ninja as war inside a  servlet container. 
   Therefore Ninja assumes now mode prod by default.

   The downside of this is that you have to set the mode for dev now manually if 
   you need it. Ninja's SuperDevMode handles this for you and you can simply
   use "mvn ninja:run" to start Ninja in dev mode.

   But if you are using e.g. Jetty's maven plugin you have to set the system
   property like that:

            <plugin>
                <groupId>org.eclipse.jetty</groupId>
                <artifactId>jetty-maven-plugin</artifactId>
                <configuration>
                   ....
                    <systemProperties>
                        <systemProperty>
                            <name>ninja.mode</name>
                            <value>dev</value>
                        </systemProperty>
                    </systemProperties>
                </configuration>
            </plugin>

   The archetypes are already equipped with the new versions.
  

to 2.3.0
---------
 
 * File naming convention for message files have changed. Previously it was
   messages.en.properties or messages.en-US.properties. Now it is 
   messages_en.properties or messages_en-US.properties. Please rename
   them in your application and you are ready to go.
   
   This change allows you to use a lot more i18n translation tools than 
   before. For instance IntelliJ and Netbeans now automatically detect the files
   as i18n files and help you with translating them efficiently.



From 1.6 to 2.0.0
-----------------

1) Ninja's testing artifacts have changed. 
Please rename the original ninja-core-test to ninja-test-utilities in your pom.xml. 
You end up with the following artifact:

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-test-utilities</artifactId>
        <version>X.X.X</version>
        <scope>test</scope>
    </dependency>
        
2) We have improved the default way xml is rendered and parsed
   and are now using "module.setDefaultUseWrapper(false)" of Jackson that produces output
   more similar to the Json renderers. This handles rendering lists and collections in a much better
   way. You can change that via annotation @JacksonXmlElementWrapper.useWrapping in your models
   Also see: the https://github.com/FasterXML/jackson-dataformat-xml


From  1.5.1 to 1.6
------------------

Please change any ftl.html accesses of the flash cookie from underscore syntax into "." syntax.
${flash_error} becomes ${flash.error}. ${flash_success} becomes ${flash.success}.
${flash_anyMessage} becomes ${flash.anyMessage}. This is now much more consistent with the general
way we access stuff inside any ftl.html file.


From 1.4 to 1.5.1
-----------------

If you are using Results.redirectTemporary(...) / Results.redirect(...) or Results.noContent()
AND if you are using a http body to indicate something (like a hyperlink text) you have to set
result.render(null) to remove the NoHttpResult. Otherwise nothing will change for you. You simply
don't need a html template by default any more.


From 1.3 to 1.4
---------------

Improvements in i18n. You can now use the following snipplet to include i18n into your templates:
${i18n("myMessageKey")}. Using ${i18nMyMessageKey} will be deprecated soon. Don't use it.

With the new i18n facilities you can now also format your messages with variables of your template:

${i18n("myMessageKey", myVariable)}.


From 1.2 to 1.3
---------------

In your project's pom.xml please replace artifactId ninja-core with ninja-servlet. 
You end up with the following dependency:

        <dependency>
            <groupId>org.ninjaframework</groupId>
            <artifactId>ninja-servlet</artifactId>
            <version>1.3</version>
        </dependency>


From 1.1 to 1.2
---------------

 * Please use "ninja.Messages" instead of "ninja.Lang" for getting messages.
 * Your web.xml must be adapted and should resemble:

<pre class="prettyprint">
&lt;web-app xmlns=&quot;http://java.sun.com/xml/ns/javaee&quot;
        xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
        xsi:schemaLocation=&quot;http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd&quot;
        version=&quot;3.0&quot;&gt;

    &lt;display-name&gt;ninja&lt;/display-name&gt;

    &lt;listener&gt;
        &lt;listener-class&gt;ninja.servlet.NinjaServletListener&lt;/listener-class&gt;
    &lt;/listener&gt;

    &lt;filter&gt;
        &lt;filter-name&gt;guiceFilter&lt;/filter-name&gt;
        &lt;filter-class&gt;com.google.inject.servlet.GuiceFilter&lt;/filter-class&gt;
    &lt;/filter&gt;
    &lt;filter-mapping&gt;
        &lt;filter-name&gt;guiceFilter&lt;/filter-name&gt;
        &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
    &lt;/filter-mapping&gt;

&lt;/web-app&gt;
</pre>    
    
From 1.0 to 1.1
---------------

no changes needed.