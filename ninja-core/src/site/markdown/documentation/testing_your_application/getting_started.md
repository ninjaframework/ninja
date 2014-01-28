Testing
=======

Testing is <b>THE MOST IMPORTANT THING</b> of Ninja. We put a huge effort into making testing your apps
as simple and quick as possible. The developers of Ninja all had experiences with different web frameworks
that were really hard to test, or where tests were not reliable and slow.

Ninja changes that. 

All test of Ninja run really fast. And they are just plain old JUnit tests. No matter if you are testing against
a mock, or a full blown Ninja application. Forget about Integration tests, and Unit tests. Ninja only knows Unit tests.


Running tests and integration into IDEs
---------------------------------------

Ninja's tests are just regular Unit tests. That means you can run all tests of your applications via a simple

<pre class="prettyprint">
    mvn test
</pre>

But sometimes you want to run only single tests from our IDE. And that is also really simple. You do not have to setup
anything special. Simply select your test class and run them as JUnit tests. That's all.

All tests by default as failsafe as possible. For instance tests with a running Ninja application do select
a free port on your machine. This also allows to simply automate your builds on your Jenkins. No more complicated
test setups to make sure that testing works. 


Tests at your disposal:
-----------------------

 * NinjaRouterTest - Making sure that your routes work.
 * Mocked Tests - Testing parts of your application in isolation.
 * NinjaTest - Testing a running server on Http level.
 * NinjaDocTester - Ideal for documenting and testing Json Apis.
 * NinjaFluentLeniumTest - The best way to test html elements via Selenium on your Ninja application.


NinjaRouterTest
---------------

NinjaRouterTest allows you to check whether your router works as expected. It allows you to make
sure that certain routes are handled by the correct controller classes and methods.

More interestingly it allows you to make sure that certain routes are only active in
an environment. For instance often you want to have method to setup test data in test and dev modes, but
not in prod mode. NinjaRouterTests allows you to assure that prod mode does not handle those kind
of methods.

<pre class="prettyprint">
    public class RoutesTest extends NinjaRouterTest {
    
        @Test
        public void testRouting() {
            
            startServerInProdMode();
            aRequestLike("GET", "/").isHandledBy(ApplicationController.class, "index");
            aRequestLike("GET", "/index").isHandledBy(ApplicationController.class, "index");
        }
        
        @Test
        public void testThatSetupIsNotAccessibleInProd() {
            
            startServerInProdMode();
            aRequestLike("GET", "/setup").isNotHandledBy(ApplicationController.class, "setup");
            
        }
    
    }
</pre>


Mocked tests
------------

Mocked tests are really fast tests. They allow you to test certain areas of your application in isolation.

Consider the following controller. This controller gets a Dao injected. And the controller method 
"postArticleJson" return a result with http code ok when the article can be posted, and a http code "not found" when
the article cannot be posted.

<pre class="prettyprint">
    public class ApiController {
        
        @Inject
        ArticleDao articleDao;
        
        public Result postArticleJson(@LoggedInUser String username,
                                  ArticleDto articleDto) {
            
            boolean succeeded = articleDao.postArticle(username, articleDto);
            
            if (!succeeded) {
                return Results.notFound();
            } else {
                return Results.ok();
            }
            
        }
    }
</pre>

Mocked tests are done via Mockito. A corresponding test would test if the controller really returns
a "ok" or "not found".

<pre class="prettyprint">

@RunWith(MockitoJUnitRunner.class)
    public class ApiControllerMockTest {
    
        @Mock
        ArticleDao articleDao;
        
        ApiController apiController;
        
        @Before
        public void setupTest() {
            apiController = new ApiController();
            apiController.articleDao = articleDao;
            
        }
        
    
        @Test
        public void testThatPostArticleReturnsOkWhenArticleDaoReturnsTrue() {
    
            when(articleDao.postArticle(null, null)).thenReturn(true);
            
            Result result = apiController.postArticleJson(null, null);
            
            assertEquals(200, result.getStatusCode());
    
        }
        
        @Test
        public void testThatPostArticleReturnsNotFoundWhenArticleDaoReturnsFalse() {
    
            when(articleDao.postArticle(null, null)).thenReturn(false);
            
            Result result = apiController.postArticleJson(null, null);
            
            assertEquals(404, result.getStatusCode());
    
        }
    
    }
</pre>

Mockito is really powerful. Please have a look at their site at: https://code.google.com/p/mockito/.
It contains a lot more documentation and wonderful examples how to test your code in a beautiful way.


NinjaTest
---------

NinjaTests really start your complete application and you are able to test all aspects of your real application.
If you use NinjaTests you get access to the NinjaTestBrowser.


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
to verify that uploading of files works, it works for post and put requests, and you can 
of course set different headers and send form requests.

We also recommend that each test method contains a method that makes sure that a database in the background
gets reset to a certain well defined state. This is done by a @Before method that calls a certain
url.

NinjaTest provide access to the application internal guice injector ( use method public Injector getInjector(); ).
This allow the verification of applications responses using internal application state information.
In the following example, the json information must match application initialization time provided by a internal service.

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



NinjaDocTester
--------------

Doctests allow to test and write html documentation at the same time. It is ideally suited to test and
document Json Apis.

<pre class="prettyprint">
    public class ApiControllerDocTest extends NinjaDocTester {
        
        String GET_ARTICLES_URL = "/api/{username}/articles.json";
        String LOGIN_URL = "/login";
        
        String USER = "bob@gmail.com";
    
        @Test
        public void testGetAndPostArticleViaJson() throws Exception {
            
            sayNextSection("Retrieving articles for a user (Json)");
        
            say("Retrieving all articles of a user is a GET request to " + GET_ARTICLES_URL);
        
            Response response = sayAndMakeRequest(
                Request.GET().url(
                        testServerUrl().path(GET_ARTICLES_URL.replace("{username}", "bob@gmail.com"))));

            ArticlesDto articlesDto = response.payloadAs(ArticlesDto.class);

            sayAndAssertThat("We get back all 3 articles of that user",
                articlesDto.articles.size(), 
                is(3));
            
        }
    
    }
</pre>

Doctests will generate html documentation into your target/site/doctester directory. In fact doctests
are quite simple. sayNextSection is a headline in a html document. say is a paragraph in a html element.
And whenever you are calling the doctest browser (eg via makeRequest(...)) the whole request, payload
and response is nicely documented in the generated html file.

This is awesome and allows you to test and document at the same time with minimal effort.
Simply extend NinjaDocTester and you are ready to test-drive your application...

But DocTester can do a lot more for you Please have a look at http://www.doctester.org for a much
more comprehensive overview. 


NinjaFluentLeniumTest
---------------------

Fluentlenium is a great tool that facilitates the usage of Selenium a lot. Selenium is the best
tool to test your application on html level. Click on elements, submit forms, make sure
css styles are present and so on.

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

NinjaDaoTestBase
----------------

Sometimes you need to test a DAO method directly on a real Database, just extend NinjaDaoTestBase and instantiate the DAO class calling the _getInstance_ method from the super class and start using it in your test methods. 

This helper starts the Persistence Service using the paramters of the application.conf file. You can pass the NinjaMode (test, dev, prod) or  set it via command line. If no NinjaMode is passed NinjaDaoTestBase assumes NinjaMode.test as default.

Check an example:

<pre class="prettyprint">
    import ninja.NinjaDaoTestBase;
    import org.junit.*;
    
    public class AbstractDaoTest extends NinjaDaoTestBase {
    	
    	private TestDao testDao;
    	
    	@Before
    	public void setup(){
    		//Instanting DAO using super method
    		testDao = getInstance(TestDao.class);
    	}
    
    
    	@Test
    	public void testSave() {
    		MyEntity myEntity = new MyEntity();
    		myEntity.setEmail("asdf@asdf.co");
    		assertNull(myEntity.getId());

    		//Use the DAO with a real database
    		myEntity = testDao.save(myEntity);

    		assertNotNull(entity.getId());
    	}
    }
</pre>
