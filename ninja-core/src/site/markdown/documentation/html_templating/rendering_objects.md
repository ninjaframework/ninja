Rendering objects in HTML views
===============================

Of course you want to make your pages dynamic.

It is simple to do so. The Freemarker engine takes any Map and you can access its members inside the view.

<pre class="prettyprint">
public Result userDashboard(
        @PathParam("email") String email,
        @PathParam("id") Integer id,
        Context context) {

    Result result = Results.html();

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
