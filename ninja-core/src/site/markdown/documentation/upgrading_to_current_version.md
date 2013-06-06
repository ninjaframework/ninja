Upgrading to latest Ninja
=========================

From 1.4 to 1.X
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
------

no changes needed.