Upgrading to latest Ninja
=========================

1.2
---

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
    
    
1.1
---

no changes needed.