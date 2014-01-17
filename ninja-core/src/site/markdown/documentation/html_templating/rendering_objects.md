Rendering objects in html views
===============================

Of course you want to make your pages dynamic.

It is simple to do so. The Freemarker engine takes any Map and you can access its member inside the view.

<pre class="prettyprint">
public Result userDashboard(
        @PathParam("email") String email,
        @PathParam("id") Integer id,
        Context context) {

    Result result = result.html();

    result.render("id", Integer.toString(id));
    result.render("email", email);

    return result;

}
</pre>

The corresponding view looks like:

<pre class="prettyprint">
&lt;html&gt;
    &lt;head&gt;
        &lt;title&gt;Dashboard for user&lt;/title&gt;
    &lt;/head&gt;
    &lt;body&gt;

        &lt;h1&gt;hi ${email}&lt;/h1&gt;

        &lt;p&gt;Your id seems to be: ${id}&lt;/p&gt;
    &lt;/body&gt;
&lt;/html&gt;
</pre>
Using simple ${email} tags you can access the content of the variables.


Advanced object parsing and rendering
-------------------------------------

The above mentioned "@PathParam("email") String email" is cool. But wouldn't it be cool under some circumstances to parse and
render objects directly - without even knowing about path parameters?

Easily possible.

1) Let's suppose you got an object like that:

<pre class="prettyprint">
public class Contact {

    private String name;
    private String email;
    public String description;

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


Really simple. Ninja maps the post request form parameters directly to the object you specified.

But wait - there is a bit more: You can even render the object directly via "Results.html().render(contact)".

The corresponding html site would then look like:

<pre class="prettyprint">
&lt;li&gt;${contact.name}&lt;/li&gt;
&lt;li&gt;${contact.email}&lt;/li&gt;
&lt;li&gt;${contact.description}&lt;/li&gt;
</pre>

"contact" is the lower camel case version of the object name. And you can then access the fields (or getters / setters in case
fields are private) via "contact.name".
