Form parsing
============

Advanced object parsing and rendering
-------------------------------------

Wouldn't it be cool under some circumstances to parse and
render objects directly - without even knowing about path parameters?

Easily possible.

1) Let's suppose you got an object like that:

<pre class="prettyprint">
public class Contact {

    private String name;
    private String email;
    public String description;
    public int id;

    public Contact() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}  
</pre>


2) And suppose you got a post form like that:

<pre class="prettyprint">
&lt;form action=&quot;/contactForm&quot; method=&quot;post&quot;&gt;
    &lt;table class=&quot;form&quot;&gt;
        &lt;tr&gt;
            &lt;th&gt;&lt;label for=&quot;name&quot;&gt; Name &lt;/label&gt;&lt;/th&gt;
            &lt;td&gt;&lt;input class=&quot;input_full&quot; type=&quot;text&quot; id=&quot;name&quot; name=&quot;name&quot; /&gt;

            &lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr&gt;
            &lt;th&gt;&lt;label for=&quot;email&quot;&gt; Email &lt;/label&gt;&lt;/th&gt;
            &lt;td&gt;&lt;input class=&quot;input_full&quot; type=&quot;email&quot; id=&quot;email&quot;
                name=&quot;email&quot; /&gt;&lt;/td&gt;
        &lt;/tr&gt;

        &lt;tr&gt;
            &lt;th&gt;&lt;label for=&quot;description&quot;&gt; Description &lt;/label&gt;&lt;/th&gt;
            &lt;td&gt;&lt;input class=&quot;input_full&quot; type=&quot;text&quot; id=&quot;description&quot;
                name=&quot;description&quot; /&gt;&lt;/td&gt;
        &lt;/tr&gt;
        
        &lt;tr&gt;
            &lt;th&gt;&lt;label for=&quot;id&quot;&gt; ID number &lt;/label&gt;&lt;/th&gt;
            &lt;td&gt;&lt;input class=&quot;input_full&quot; type=&quot;number&quot; id=&quot;id&quot;
                name=&quot;id&quot; /&gt;&lt;/td&gt;
        &lt;/tr&gt;
    &lt;/table&gt;

    &lt;p&gt;
        &lt;input type=&quot;submit&quot; value=&quot;Send&quot; /&gt; &lt;input type=&quot;reset&quot;
            value=&quot;Reset&quot;&gt;
    &lt;/p&gt;
&lt;/form&gt;
</pre>

3) You can then parse that request simply by specifying the object in the application controller:

<pre class="prettyprint">
public Result postContactForm(Context context, Contact contact) {

    return Results.html().render(contact);
}
</pre>


Really simple. Ninja maps the post request form parameters directly to the object you specified. Primitive types 
(or their object wrapper types) will be automatically converted and mapped.


But wait - there is a bit more: You can even render the object directly via "Results.html().render(contact)".

The corresponding Freemarker template would then look like:

<pre class="prettyprint">
&lt;li&gt;${contact.name}&lt;/li&gt;
&lt;li&gt;${contact.email}&lt;/li&gt;
&lt;li&gt;${contact.description}&lt;/li&gt;
&lt;li&gt;${contact.id}&lt;/li&gt;
</pre>

"contact" is the lower camel case version of the object's class name. 
And you can then access the fields (or getters / setters in case the
fields are private) via "contact.name".
