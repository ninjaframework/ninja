NinjaRouterTest
===============

Introduction
------------

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