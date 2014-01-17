Upgrading to latest Ninja
=========================

to 2.6.0
--------

<code>SessionCookie</code> and <code>FlashCookie</code> changed their names. 
<code>SessionCookie</code> is now called
<code>Session</code> and <code>FlashCookie</code> is called <code>FlashScope</code>.

<code>Context</code> object reflects this by providing <code>getSession()</code> 
and <code>getFlashScope()</code> methods.


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

    <web-app xmlns="http://java.sun.com/xml/ns/javaee"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
            version="3.0">

        <display-name>ninja</display-name>

        <listener>
            <listener-class>ninja.servlet.NinjaServletListener</listener-class>
        </listener>

        <filter>
            <filter-name>guiceFilter</filter-name>
            <filter-class>com.google.inject.servlet.GuiceFilter</filter-class>
        </filter>
        <filter-mapping>
            <filter-name>guiceFilter</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>

    </web-app>
    
    
From 1.0 to 1.1
---------------

no changes needed.