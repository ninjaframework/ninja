Working with Json
=================

Ninja provides out of the box support to render arbitrary Java objects as Json as
well as parsing of Json into Java objects.


Rendering Json
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

This controller will produce a nicely formatted Json output:

<pre class="prettyprint">
{'name':'John Johnson'}
</pre>


Parsing Json
------------

If you want to parse incoming json requests you simply have to add the mapping pojo
to the controller method signature.

Consider the following Json:

<pre class="prettyprint">
{'name':'John Johnson'}
</pre>

This Json maps to the following Java pojo:

<pre class="prettyprint">
package models;

public class Person {       
    String name;
}    
</pre>


If you send that Json to your application via a post request you only need to 
add class Pojo to the controller method and Ninja will pase the incoming
Json for you:

<pre class="prettyprint">
package controllers;

public class ApplicationController {       

    public Result parsePerson(Person person) {
        
        String nameOfPerson = person.name; // will be John Johnson
        ...

    }
}
</pre>


Advanced Json usage
-------------------

Under the hood Ninja uses Jackson (http://jackson.codehaus.org/). Jackson
is one of the most widely used Json serializers of the Java econsystem.

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
after is has been used to parse or generate Json.

More on Jackson modules: http://wiki.fasterxml.com/JacksonFeatureModules