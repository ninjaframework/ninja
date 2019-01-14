RecycledNinjaServerTester
=========================

Introduction
------------

RecycledNinjaServerTester provides much of the same functionality as NinjaTest,
but starts up a single Ninja test server for all tests in a Junit test class.

If you're unit tests do not rely on a fresh server then this approach will
dramatically increase the speed of your unit tests.

<pre class="prettyprint">
public class MyControllerTest extends RecycledNinjaServerTester {

    @Test
    public void testThatHomepageWorks() {
        NinjaTestBrowser ninjaTestBrowser = new NinjaTestBrowser();

        // redirect will send a location: redirect in the headers
        String result = ninjaTestBrowser.makeRequest(withBaseUrl("/"));

        // If the redirect has worked we must see the following text
        // from the index screen:
        assertTrue(result.contains("Hello to the blog example!"));
        assertTrue(result.contains("My second post"));
    }
 
}
</pre>

If you want to revert back to a single Ninja test server for each test method,
you can simply extend your class from FreshNinjaServerTester.