Html templating
===============

Well. Html is at the core of this internet thing. Ninja
uses Freemarker as templating engine.

If you want to know all advanced stuff you can do with Freemarker
please check out their [excellent manual](http://freemarker.sourceforge.net/).


Basic html templating
---------------------

Templates are stored in folder views/ .

And a basic template like - for instance /views/basic.ftl.html simply looks like:

    <html>
        <head>
            <title>My title</title>
        </head>
    <html>

Does not look too special. And it is not.


Using subviews nested templates
-------------------------------

Usually you want to reuse parts of your application. Let's say a body. And a footer.
Freemarker makes that quite simple.

defaultLayout.ftl.html:

    <#macro myLayout title="Layout example">
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>${title}</title>
    <body>

        <#nested/>

        <#include "footer.ftl.html"/>
        
    </body>
    </html>
    </#macro>

There are some interesting things:

 * <#macro myLayout title="Layout example"> defines a variable $title a default tile for it
 * a variable in Freemarker looks like 
 * Nesting other views into it is done by <#nested/>
 * And simply including another view is done by <#include "footer.ftl.html"/>
 
footer.ftl.html looks like:

    <hr>
    <footer>
        <p>Company 2012</p>
    </footer>

... nothing special here...


And the main view "basic.ftl.html" then looks like:

    <#import "defaultLayout.ftl.html" as layout> 
    <@layout.myLayout "Home page">    

        <h1>my text</h1>

    </@layout.myLayout>


There are some interesting things:

 * We import our main template <#import "defaultLayout.ftl.html" as layout>
 * We also redefine the default title to "Home Page"
 * The rest of the template will be included where <#nested/> in the defaultLayout.ftl.html is written.
 
When you render things you would call Results.html().template("basic.ftl.html") in your controller method.
 
And that's all.



Parameters and render stuff in the view
--------------------------------------

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

    <html>
        <head>
            <title>Dashboard for user</title>
        </head>
        <body>
    
            <h1>hi ${email}</h1>

            <p>Your id seems to be: ${id}</p>
        </body>
    </html>

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


    <form action="/contactForm" method="post">
        <table class="form">
            <tr>
                <th><label for="name"> Name </label></th>
                <td><input class="input_full" type="text" id="name" name="name" />

                </td>
            </tr>
            <tr>
                <th><label for="email"> Email </label></th>
                <td><input class="input_full" type="email" id="email"
                    name="email" /></td>
            </tr>

            <tr>
                <th><label for="description"> Description </label></th>
                <td><input class="input_full" type="text" id="description"
                    name="description" /></td>
            </tr>
        </table>

        <p>
            <input type="submit" value="Send" /> <input type="reset"
                value="Reset">
        </p>
    </form>


3) You can then parse that request simply by specifying the object in the application controller:

<pre class="prettyprint">
    public Result postContactForm(Context context, Contact contact) {

        return Results.html().render(contact);
    }
</pre>


Really simple. Ninja maps the post request form parameters directly to the object you specified.

But wait - there is a bit more: You can even render the object directly via "Results.html().render(contact)".

The corresponding html site would then look like:


    <li>${contact.name}</li>
    <li>${contact.email}</li>
    <li>${contact.description}</li>


"contact" is the lower camel case version of the object name. And you can then access the fields (or getters / setters in case
fields are private) via "contact.name".


Security and templating
-----------------------

Ninja is html escaping all variables that you render by default. If you don't want to do that you
can use the noescape directive around that particular variable.

    <#noescape>${yourVariableThatShouldNotBeEscaped}</#noescape>



i18n and the view
----------------

You can access all messages by using a simple <html>

Lets say your message.properties looks like:

    casinoRegistrationTitle=My funky title
    

You can then access the variable inside your view like that.

    <html>
        <head>
            <title>${i18n("casinoRegistrationTitle")}</title>
        </head>
    <html>


flash content in the view
-------------------------

The flash scope of the application is important. It helps to maintain
a little bit of state between requests even on
a restful architecture.

Rendering the flash error and success messages is straight forward.

Please note that all variables of the flash scope are prefixed with
"flash_".

A simple demo of that
  
    <#if flash_error??>
            <p class="error">${flash_error}</p>
    </#if>

    <#if flash_success??>
            <p class="success">${flash_success}</p>
    </#if>

This simply checks if a flash error or success is there and prints the message out.
The message itself is being translated.



Implicit variables available in templates
-----------------------------------------

 * <code>${session.*}</code> You can access all session-cookie values by their keys prefixed with the accessor "session.". E.g.: 
   If you had set a cookie with the key "username", then you can use ${session.username} to resolve the 
   username and display it.
 * <code>${flash.success}</code> Translated (if possible) flash success message (via success("value")).
 * <code>${flash.error}</code> Translated (if possible) flash error message (via error("value")).
 * <code>${flash.*}</code> Translated (if possible) flash message with arbitrary key (via put("key", "value")).
 * <code>${lang}</code> resolves to the language Ninja uses currently. 
 * <code>${contextPath}</code> resolves the context path of the application (empty if running on root)


