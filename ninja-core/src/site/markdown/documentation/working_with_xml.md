Working with Xml
=================

Ninja provides out of the box support to render arbitrary Java objects as Xml as
well as parsing of Xml into Java objects.


Rendering Xml
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

        return Results.xml().render(person);

    }
}
</pre>

This controller will produce a nicely formatted Xml output:

<pre class="prettyprint">
&lt;Person&gt;&lt;name&gt;John Johnson&lt;/name&gt;&lt;/Person&gt;
</pre>


Parsing Xml
------------

If you want to parse incoming xml requests you simply have to add the mapping pojo
to the controller method signature.

Consider the following Xml:

<pre class="prettyprint">
&lt;Person&gt;&lt;name&gt;John Johnson&lt;/name&gt;&lt;/Person&gt;
</pre>

This Xml maps to the following Java pojo:

<pre class="prettyprint">
package models;

public class Person {       
    String name;
}    
</pre>


If you send that Xml to your application via a post request you only need to 
add class Pojo to the controller method and Ninja will pase the incoming
Xml for you:

<pre class="prettyprint">
package controllers;

public class ApplicationController {       

    public Result parsePerson(Person person) {
        
        String nameOfPerson = person.name; // will be John Johnson
        ...

    }
}
</pre>


Advanced Xml usage
-------------------

Under the hood Ninja uses Jackson (http://jackson.codehaus.org/). Jackson
is one of the most widely used Xml serializers of the Java ecosystem.

If you want to customize the way Jackson works you can do so by injecting
XmlMapper into a startup action and modifying it. 

<pre class="prettyprint">
@Singleton
public class MyXmlMapper {

    @Inject 
    XmlMapper xmlMapper;

    @Start(order = 90)
    public void configureXmlMapper() {
        // Adding Joda Time parsing and rendering support to Jackson
        xmlMapper.registerModule(new JodaModule());     
    }
}
</pre>

XmlMapper is a singleton and can be modified and extended 
by your application. 


It is safe to modify XmlMapper before
it is actually used, but it is not threadsafe to modify XmlMapper 
after is has been used to parse or generate Xml.

More on Jackson modules: http://wiki.fasterxml.com/JacksonFeatureModules