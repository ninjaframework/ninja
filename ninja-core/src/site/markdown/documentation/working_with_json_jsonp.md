Working with JSON
=================

Ninja provides out of the box support to render arbitrary Java objects as JSON as
well as parsing of JSON into Java objects.


Rendering JSON
--------------

Consider this simple model class:

<pre class="prettyprint">
package models;

public class Person {       
    String name;
}    
</pre>

And this controller:

<pre class="prettyprint">
package controllers;

public class ApplicationController {       

    public Result index() {

        Person person = new Person();
        person.name = "John Johnson";

        return Results.json().render(person);

    }
}
</pre>

This controller will produce a nicely formatted JSON output:

<pre class="prettyprint">
{"name":"John Johnson"}
</pre>


Parsing JSON
------------

If you want to parse incoming JSON requests you simply have to add the mapping POJO
to the controller method signature.

Consider the following JSON:

<pre class="prettyprint">
{"name":"John Johnson"}
</pre>

This JSON maps to the following Java POJO:

<pre class="prettyprint">
package models;

public class Person {       
    String name;
}    
</pre>


If you send that JSON to your application via the HTTP body you only need to 
add the POJO class to the controller method and Ninja will parse the incoming
JSON for you:

<pre class="prettyprint">
package controllers;

public class ApplicationController {       

    public Result parsePerson(Person person) {
        
        String nameOfPerson = person.name; // will be John Johnson
        ...

    }
}
</pre>


Rendering JSONP
---------------

Rendering JSONP (JSON wrapped by Javascript function call) is quite
similar to rendering plain JSON:

<pre class="prettyprint">
Results.jsonp().render(person);
</pre>

The only important difference that the function name to render must be
defined via parameter <code>callback</code>. For instance
<code>?callback=MyApp.Path.myCallback123</code> would then produce
the following output:

<pre class="prettyprint">
MyApp.Path.myCallback123({"response":"data"})
</pre>

You can change the name of the callback parameter (<code>callback</code> by
default) in your <code>application.conf</code> via

<pre class="prettyprint">
ninja.jsonp.callbackParameter=... // specify your custom callback parameter name
</pre>

<div class="alert alert-info">
The value of the callback parameter is sanitized for security reasons. Only
plain callback parameter values are possible. Something like 
<code>?callback=MyApp.Path.myCallback123</code> 
works, but <code>?callback=alert(document.cookie)</code> does not work.
</div>


Advanced JSON usage
-------------------

Under the hood Ninja uses Jackson (http://wiki.fasterxml.com/JacksonHome). Jackson
is one of the most widely used JSON serializers of the Java ecosystem.

If you want to customize the way Jackson works you can do so by injecting
ObjectMapper into a startup action and modifying it. 

<pre class="prettyprint">
@Singleton
public class MyObjectMapper {

    @Inject 
    ObjectMapper objectMapper;

    @Start(order = 90)
    public void configureObjectMapper() {
        // Adding Joda Time parsing and rendering support to Jackson
        objectMapper.registerModule(new JodaModule());     
    }
}
</pre>

ObjectMapper is a singleton and can be modified and extended 
by your application. 


It is safe to modify ObjectMapper before
it is actually used, but it is not threadsafe to modify ObjectMapper 
after is has been used to parse or generate JSON.

More on Jackson modules: http://wiki.fasterxml.com/JacksonFeatureModules


### Jackson JSON Views

Ninja also supports Jackson's JSON Views: with `@JsonView` annotations 
you can easily define which properties of an object you want to include 
in the JSON output. A simple example where only the "name" field will be
included:

<pre class="prettyprint">
public class AppController {

    public Result jsonPerson() {
        Person person = new Person();
        person.name = "John Doe";
        person.age = 56;
        
        return Results.json().jsonView(View.Public.class).render(person);
    }
    
    static class Person {
        @JsonView(View.Public.class)
        public String name;
        
        @JsonView(View.Private.class)
        public Integer age;
    }
    
    static class View {
        static class Public {}
        static class Private {}
    }
}
</pre>

More on Jackson's JSON Views: http://wiki.fasterxml.com/JacksonJsonViews
