NinjaFluentLeniumTest
=====================

Introduction
------------

Fluentlenium is a great tool that facilitates the usage of Selenium a lot. Selenium is the best
tool to test your application on HTML level. Click on elements, submit forms, make sure
CSS styles are present and so on.

Ninja integrates FluentLenium via the NinjaFluentLeniumTest. 
Simply extend the class and you are ready to go:


<pre class="prettyprint">
public class ApplicationControllerFluentLeniumTest extends NinjaFluentLeniumTest {

    @Test
    public void testThatHomepageWorks() {

        goTo(getServerAddress() + "/");

        System.out.println("title: " + title());

        assertTrue(title().contains("Home page"));

        click("#login");

        assertTrue(url().contains("login"));


    }

}
</pre>

But FluentLenium can do a lot more. Check out https://github.com/FluentLenium/FluentLenium


From 0.10.3 to 3.2.0
--------------------

Prior to Ninja 6.0.0, FluentLenium 0.10.3 was used. Upgrading to version 3.2.0 breaks backward compatibility
of older tests. If you are facing this case, you can either follow the migration guide proposed by FluentLenium
(http://fluentlenium.org/migration/from-0.13.2-to-1.0-or-3.0/) ; or you can force your project to use
the older version of FluentLenium by setting up that version in your project <code>pom.xml</code> dependencies.

<pre class="prettyprint">
<dependency>
    <groupId>org.fluentlenium</groupId>
    <artifactId>fluentlenium-core</artifactId>
    <version>0.10.3</version>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
</pre>
