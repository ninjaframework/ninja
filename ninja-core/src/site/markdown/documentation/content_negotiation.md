Content negotiation
===================

A http request by a client usually sends a header called "Accept:...". That header signals
the web server (and hence Ninja) what kind of content the client wants to get.

For instance a regular web browser sends a header "Accept:text/html" and wants
to get back html content. An API client would send a "Accept:application/json"
to signal that the web server should send the content as json.

That means that one route like myserver.com/api/person can return the result
in different formattings and switch the content based on the Accept header sent
by the request.

By the way - in reality content negotiation can become quite complex. There is 
a good article on wikipedia about it: http://en.wikipedia.org/wiki/Content_negotiation

<div class="alert alert-info">
Content negotiation is very cool - but sometimes it adds more complexity than
needed. Another way is defining different routes for different result types.
myserver.com/api/person.json for json, myserver.com/api/person.xml for xml
and so on.
</div>



Basic behavior of a Ninja Result
--------------------------------

A Ninja result offers two methods to tweak the content negotiation behavior:

- <code>result.supportedContentTypes(...)</code>
- <code>result.fallbackContentType(...)</code>

### supportedContentTypes(...)

supportedContentTypes allows you to specify which content types this result
is supposed to handle.

Let's say you got a Person object:
<pre class="prettyprint">
class Person {
    String name;
}
</pre>

And you want a controller that handles xml and json. Then you'd specify the following
route and controller:

<pre class="prettyprint">
  router.GET().route("/api/person").with(MyController.class, "getPerson");
</pre>

and

<pre class="prettyprint">
public Result getPerson() {
  Person person = new Person();
  person.name = "Fritz Fritzensen";

  return Results
           .ok()
           .supportedContentTypes(
             Result.APPLICATION_JSON, 
             Result.APPLICATION_XML);
}
</pre>

Route /api/person would then handle Accept:application/json and Accept:application/xml.
The cool thing is that you write your code once, but Ninja handles the rendering
via the matching rendering engine for you. If the Accept type dies not match you'll
get a bad request error response.


### fallbackContentType(...)

In the example above we saw that you'll get a bad request error response when
a content type does not match. But sometimes you want to nevertheless render
something. That's what fallbackContentType is for. 

If we'd extend the result like that:

<pre class="prettyprint">
public Result getPerson() {
  Person person = new Person();
  person.name = "Fritz Fritzensen";

  return Results
           .ok()
           .supportedContentTypes(
             Result.APPLICATION_JSON, 
             Result.APPLICATION_XML)
           .fallbackContentType(Result.APPLICATION_JSON);
}
</pre>

Extending the result via <code>.fallbackContentType(Result.APPLICATION_JSON)</code> 
means that we'll always get a json response as callback. That's the case even
when the Accept header was something completely unsupported by this request.

### The default behavior

If you do not set anything and just create a new result via e,g, <code>Results.ok().render(myObject)</code>
the following rules apply:

1. The result will handle json, xml and html. That means you have to make sure
that your model can be rendered properly with json and xml - and that you also
provide a .ftl.html file that renders the html representation.
2. If the Accept header does not match any of the three default accept types a
400 - bad request will be returned.

Advanced settings
-----------------

You can always use a filter to set default values on all controller methods
or controller classes.

The following filter for instance will set the results to handle json and
xml and use json as fallback:

<pre class="prettyprint">
public class XmlAndJsonResult implements Filter {

  @Override
  public Result filter(FilterChain chain, Context context) {

    Result result = chain.next(context);
    return result
             .supportedContentTypes(
               Result.APPLICATION_JSON, 
               Result.APPLICATION_XML)
             .fallbackContentType(Result.APPLICATION_JSON);
    }
}
</pre>
An annotated controller method then looks like:

<pre class="prettyprint">
@FilterWith(XmlAndJsonResult.class)
public Result getPerson() {
  Person person = new Person();
  person.name = "Fritz Fritzensen";

  //The filter will set all content negotiation related stuff for us:
  return Results.ok();
}
</pre>
