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

        goTo(getBaseUrl() + "/");

        System.out.println("title: " + window().title());

        assertTrue(window().title().contains("Home page"));

        $("#login").click();

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
&lt;dependency&gt;
    &lt;groupId>org.fluentlenium&lt;/groupId&gt;
    &lt;artifactId>fluentlenium-core&lt;/artifactId&gt;
    &lt;version>0.10.3&lt;/version&gt;
    &lt;scope>test&lt;/scope&gt;
    &lt;exclusions&gt;
        &lt;exclusion&gt;
            &lt;groupId&gt;commons-logging&lt;/groupId&gt;
            &lt;artifactId&gt;commons-logging&lt;/artifactId&gt;
        &lt;/exclusion&gt;
    &lt;/exclusions&gt;
&lt;/dependency&gt;
</pre>
