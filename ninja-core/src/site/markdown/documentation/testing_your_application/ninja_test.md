NinjaTest
=========

Introduction
------------

NinjaTests start your application with a real HTTP server and are full integration
tests. They allow you to test all aspects of your application.

You can use NinjaTestBrowser to access and retrieve content from your started
server.

<pre class="prettyprint">
public class ApplicationControllerTest extends NinjaTest {

    @Before
    public void setup() {

        ninjaTestBrowser.makeRequest(getServerAddress() + "setup");

    }

    @Test
    public void testThatHomepageWorks() {

        // /redirect will send a location: redirect in the headers
        String result = ninjaTestBrowser.makeRequest(getServerAddress() + "/");

        // If the redirect has worked we must see the following text
        // from the index screen:
        assertTrue(result.contains("Hello to the blog example!"));
        assertTrue(result.contains("My second post"));

    }

}
</pre>

The testcase illustrates that the NinjaTestBrowser can make request, and that you can then check
if the request contained certain content.

The NinjaTestBrowser can do much more than simply getting content. It can for instance be used
to verify that uploading of files works, it works for POST and PUT requests, and you can 
of course set different headers and send form requests.

We also recommend that each test method contains a method that makes sure that 
a database in the background
gets reset to a certain well defined state. 
This is done by a @Before method that calls a certain URL.

NinjaTest provides access to the application internal Guice injector
 (use method <code>public Injector getInjector();</code> ).
This allow the verification of applications responses using internal 
application state information. In the following example, the JSON information 
must match application initialization time provided by an internal service.

<pre class="prettyprint">
public class ApplicationInjectorTest extends NinjaTest {

    @Test
    public void testThatInjectorAccessibleFromNinjaTestIsTheApplicationInjector() {

        // this is the application guice injector
        Injector injector = getInjector();

        // We know that this service is a singleton and it provides the application initialization time.
        long serviceInitializationTime = injector.getInstance(GreetingService.class).getServiceInitializationTime();

        // provide a json with information about the application initialization time.
        String serviceInitTimeResult = ninjaTestBrowser.makeJsonRequest(getServerAddress() + "/serviceInitTime");

        //The response information must match the internal application state
        assertEquals("{\"initTime\":" + serviceInitializationTime + "}", serviceInitTimeResult);

    }

}
</pre>