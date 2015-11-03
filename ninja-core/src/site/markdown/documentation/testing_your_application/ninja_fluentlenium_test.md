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
