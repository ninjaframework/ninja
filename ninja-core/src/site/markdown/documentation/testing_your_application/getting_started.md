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


Tests at your disposal:
-----------------------

 * <code>Mocked Tests</code> - Testing parts of your application in isolation.
 * <code>NinjaTest</code> - Testing a running server on HTTP level.
 * <code>NinjaDocTester</code> - Ideal for documenting and testing JSON APIs.
 * <code>NinjaFluentLeniumTest</code> - The best way to test HTML elements via Selenium on your Ninja application.
