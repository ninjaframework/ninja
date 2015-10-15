Mocked tests
============

Introduction
------------

Mocked tests are really fast tests. They allow you to test certain areas of your application in isolation.

Consider the following controller. This controller gets a Dao injected. And the controller method 
"postArticleJson" return a result with an HTTP code "ok" when the article can be posted, and an HTTP code "not found" when
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
an "ok" or "not found".

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

Mockito is really powerful. Please have a look at their site at: http://mockito.org/.
It contains a lot more documentation and wonderful examples how to test your code in a beautiful way.