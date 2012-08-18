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
 * a variable in Freemaker looks like 
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



Paramters and render stuff in the view
--------------------------------------

Of course you want to make your pages dynmic.

It is simple to do so. The freemarker engine takes any Map and you can access its member inside the view.

<pre class="prettyprint">
    public Result userDashboard(
            @PathParam("email") String email,
            @PathParam("id") Integer id,
            Context context) {

        Map<String, Object> map = new HashMap<String, Object>();
        // generate tuples, convert integer to string here because Freemarker does it in locale
        // dependent way with commas etc
        map.put("id", Integer.toString(id));
        map.put("email", email);

        //and render page with both parameters:
        return Results.html().render(map);
    }
</pre>

The corresponding view looks like:

    <html>
        <head>
            <title>Dasboard for user</title>
        </head>
        <body>
    
            <h1>hi ${email}</h1>

            <p>Your id seems to be: ${id}</p>
        </body>
    </html>

Using simple ${email} tags you can access the content of the variables.


Security and templating
-----------------------

Ninja is html escaping all variables that you render by default. If you don't want to do that you
can use the noescape directive around that particular variable.

    <#noescape>${yourVariableThatShouldNotBeEscaped}</#noescape>



18n and the view
----------------

Cou can access all messages by using a simple <html>

Lets say your message.properties looks like:

    i18nCasinoRegistrationTitle=My funky title
    

You can then access the variable inside your view like that.

    <html>
        <head>
            <title>${i18nCasinoRegistrationTitle}</title>
        </head>
    <html>

**Note**
 * You must use camelCase in your messages.properties file.
 * You must prefix your messages with i18n 


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
    