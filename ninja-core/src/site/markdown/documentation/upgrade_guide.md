Upgrade guide
=============

Sometimes (and hopefully not too often) we have to introduce breaking changes
into Ninja's behavior. This document describes which steps are needed to upgrade
your application to the latest Ninja version. Simply start with your current 
version and then work your way up to the top of the document.

to 7.0.0
--------

Version should be a drop-in replacement if using v6.8.2, but with Google guice v6.0.0
you may need to ensure any of your projects dependencies are not bringing in Guava version older
than v31 (older versions will cause injection to fail)

to 6.8.2
--------

Flyway has been updated to prior version 8.2.2. As mentioned in the Flyway
documentation there is a small change with MySQL. Since the version 8.2.x,
MySQL Driver is not included any more in Flyway distribution due to License.
MariaDB will be used as fallback driver if no MySQL driver is present on the
Classpath.

If you still want to use a MySQL with MySQL Driver, you have to add new 
dependencies on your project.

```java
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-mysql</artifactId>
        <version>8.2.2</version>
    </dependency>
```

```java
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
       <version>8.0.27</version>
   </dependency>
```


to 6.7.0
--------

JPA and Flyway have been migrated to a separate maven module. You can 
check the configuration inside https://github.com/ninjaframework/ninja/tree/develop/ninja-servlet-jpa-blog-integration-test

But in general you have to do the following:

```java

        <dependency>
            <groupId>org.ninjaframework</groupId>
            <artifactId>ninja-db-classic</artifactId>
            <version>${ninja.version}</version>
        </dependency>
```

Modify your Module java to start-up the database support:


```java
@Singleton
public class Module extends AbstractModule {
    
    private final NinjaProperties ninjaProperties;
    
    public Module(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }
    
    @Override
    protected void configure() { 
        
        install(new JpaModule(ninjaProperties));
        install(new MigrationClassicModule());

        // ... likely more modules...
    }
}
```


to 6.6.0
--------

- Use ?no_enc instead of noescape. Due to changes in our html templating engine
  we changed the way how to render unescaped content. Make sure to use something like
  ${yourVariableThatShouldNotBeEscaped?no_esc} instead of &lt;#noescape&gt;${yourVariableThatShouldNotBeEscaped}&lt;/#noescape&gt;.



to 6.3.X
--------

Some inner methods of the `Ninja` base class have a new signature, with more 
precise exception types. If you overrided some of them, or if you implemented 
your own `Ninja` class without extending the `NinjaDefault` one, you should be 
able to fix them easily.

to 6.3.0
--------

Postoffice is now a separate dependency. Make sure to add the dependency to 
your pom.xml file and add the module to Modules.java.
See details in https://www.ninjaframework.org/documentation/sending_mail.html

to 6.0.1
--------

### FluentLenium update causes backward incompatibility

Refer to the FluentLenium migration guide http://fluentlenium.org/migration/from-0.13.2-to-1.0-or-3.0/ 
or force usage of fluentlenium-core 0.10.3 in your project dependencies.

to 6.0.0
--------

- Java 8 is required.
- Maven 3.3.9 is required.
- Replace all usages of Guava’s Optional with Java8 Optional. Your compiler
  will guide you.

to 5.4.0
--------

Guice bindings for template engines like Freemarker were modified to be bound
differently than in previous versions.  If you use a custom template engine
via a third party module then it may need to be bound into Guice slightly
different.  This is how template engines typically were bound:

```java
public class CustomTemplateEngineModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TemplateEngine.class).to(CustomTemplateEngine.class);
    }
}
```

This is how template engines need to be bound as of v5.4.0:

```java
public class CustomTemplateEngineModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CustomTemplateEngine.class);
    }
}
```

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
