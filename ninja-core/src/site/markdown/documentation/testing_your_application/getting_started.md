Testing
=======

Testing is <b>THE MOST IMPORTANT THING</b> of Ninja. We put a huge effort into making testing your apps
as simple and quick as possible. The developers of Ninja all had experiences with different web frameworks
that were really hard to test, or where tests were unreliable and slow.

Ninja changes that. 

All tests of Ninja run really fast. And they are just plain old JUnit tests. No matter if you are testing against
a mock, or a full blown Ninja application. Forget about Integration tests, and Unit tests. Ninja only knows Unit tests.


Running tests and integration into IDEs
---------------------------------------

Ninja's tests are just regular Unit tests. That means you can run all tests of your applications via a simple

<pre class="prettyprint">
mvn test
</pre>

But sometimes you want to run only a single test from our IDE. And that is also really simple. You do not have to setup
anything special. Simply select your test class and run them as JUnit tests. That's all.

All tests by default are as failsafe as possible. For example, tests that start a Ninja application select
a free port on your machine. This also allows to simply automate your builds on your Jenkins. No more complicated
test setups to make sure that testing works. 


# Tests at your disposal
----------------------

 * <code>Mocked Tests</code> - Testing parts of your application in isolation.
 * <code>NinjaTest</code> - Testing a running server on HTTP level.
 * <code>NinjaDocTester</code> - Ideal for documenting and testing JSON APIs.
 * <code>NinjaFluentLeniumTest</code> - The best way to test HTML elements via Selenium on your Ninja application.

# NinjaTestServer (Advanced topic)

All integration tests are backed by the NinjaTestServer. In NinjaTest this functionality is hidden
to be a bit more user-friendly. But if you want to use all advanced features you may
want to use the NinjaTestServer directly.

Turns out it is not too complicated.

A handmade test with a running Ninja server could look like this:

<pre class="prettyprint">
@Test
public void showcaseBasicTest() {

    // Creating a TestServer is done like this
    NinjaTestServer ninjaTestServer = NinjaTestServer.builder().build();

    // baseUrl is something like &quot;http://localhost:5565&quot;
    String baseUrl = ninjaTestServer.getBaseUrl();

    // ... using this url you can now call endpoints, verify and test your application


    // You can also get instances that are initialized by Guice out of your testserver.
    // This is often useful if you want to assert functionality programmatically.
    // Like testing repositories and so on.
    MyRepository myRepository = ninjaTestServer.getInjector().getInstance(MyRepository.class);

    Assertions.assertThat(myRepository.getNumberOfUsers()).isEqualTo(1);

    // Don't forget to shut down that instance.
    // You may also want to put this in a @After JUnit method.
    ninjaTestServer.shutdown();
}
</pre>

## Overriding properties from your tests

Overriding properties programmatically is very important.
A usecase is testcontainers.org. You can start a testcontainers postgresql
docker instance. But you have to tell Ninja about the Jdbc url.

The testcontainer port will be random, so you can't just put a static Jdbc url in your applcation.conf.

Using overrideProperties(...) you can set properties that will override any other properties
present in Ninja's configuration.


<pre class="prettyprint">
public class DatabaseBaseTest {

    // Let's assume ARunningPostgresServer encapsulates a Testcontainer running
    // a Postgres instance.
    // More here: https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/
    // Once the Postgres Docker container is running Testcontainers can return as the jdbc url
    public String jdbcUrl = ARunningPostgresServer.getJdbcUrl();

    @Before
    @Override
    public void showcase_test() {
        // Now we start Ninja. 
        // Make sure we set the property. That property will then be picked up
        // by anything that gets properties from NinjaProperties like for instance Flyway and NinjaDb.
        NinjaTestServer ninjaTestServer = NinjaTestServer.builder()
            .overrideProperties(ImmutableMap.of(&quot;application.datasource.default.url&quot;, jdbcUrl))
            .build();

        // ... now you can do your tests on that Ninja instance


        // ... at the end we again shut down Ninja
        ninjaTestServer.shutdown();
    }
</pre>

## Overriding Guice bindings from your tests

<pre class="prettyprint">
@Test
public void testCase() {

    CacheRepository cacheRepositoryMock = Mockito.mock(CacheRepository.class);

    // We create a module. All bindings inside this module will later
    // override any other bindings that Ninja would setup "normally".
    AbstractModule module = new AbstractModule() {
        @Override
        protected void configure() {
            bind(CacheRepository.class).toInstance(cacheRepositoryMock);
        }
    };

    NinjaTestServer ninjaTestServer = NinjaTestServer.builder()
        .overrideModule(module)
        .build();


    // CacheRepository inside our running Ninja will use cacheRepositoryMock.
    // All instances inside Ninja that inject CacheRepository will get 
    // cacheRepositoryMock.
    ///
    // We can now invoke things on - for instance - a service and verify via Mockito 
    // whether the correct methods on cacheRepositoryMock have been called.
    //
    // There are endless options to mock and test tings partially in Ninja.

    // ... at the end we again shut down Ninja
    ninjaTestServer.shutdown();
}
</pre>




