NinjaDocTester
==============

Introduction
------------

Doctests allow to test and write HTML documentation at the same time. 
It is ideally suited to test and document JSON APIs.

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

Doctests will generate HTML documentation into your 
<code>target/site/doctester</code> directory. In fact doctests
are quite simple. <code>sayNextSection</code> is a headline in an HTML document. 
<code>say</code> is a paragraph in an HTML element.
And whenever you are calling the doctest browser (eg via makeRequest(...)) 
the whole request, payload and response is nicely documented in the 
generated HTML file.

This is awesome and allows you to test and document at the same time with minimal effort.
Simply extend NinjaDocTester and you are ready to test-drive your application...

But DocTester can do a lot more for you Please have a look at http://www.doctester.org for a much
more comprehensive overview. 
