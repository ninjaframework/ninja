Working with Xml
================

This is almost exactly the same as rendering Json.
Let's use again a simple Person Pojo:

Consider this simple model class:

<pre class="prettyprint">

    package models;

    public class Person {       
    
        String name;
    }
    
</pre>

Rendering is done by using Results.xml.render(...) like so:

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

As in the case we are using Jackson under the hood the does the transformation work.

