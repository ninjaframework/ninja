Working with XML
=================

Ninja provides out of the box support to render arbitrary Java objects as XML as
well as parsing of XML into Java objects.


Rendering XML
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

This controller will produce a nicely formatted XML output:

<pre class="prettyprint">
&lt;Person&gt;&lt;name&gt;John Johnson&lt;/name&gt;&lt;/Person&gt;
</pre>


Parsing XML
------------

If you want to parse incoming XML requests you simply have to add the mapping POJO
to the controller method signature.

Consider the following XML:

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


If you send that XML to your application via the HTTP body you only need to 
add POJO class to the controller method and Ninja will pase the incoming
XML for you:

<pre class="prettyprint">
package controllers;

public class ApplicationController {       

    public Result parsePerson(Person person) {
        
        String nameOfPerson = person.name; // will be John Johnson
        ...

    }
}
</pre>


Advanced XML usage
-------------------

Under the hood Ninja uses Jackson (http://wiki.fasterxml.com/JacksonHome). Jackson
is one of the most widely used XML serializers of the Java ecosystem.

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
after is has been used to parse or generate XML.

More on Jackson modules: http://wiki.fasterxml.com/JacksonFeatureModules