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

<pre class="prettyprint">
&lt;html&gt;
    &lt;head&gt;
        &lt;title&gt;My title&lt;/title&gt;
    &lt;/head&gt;
&lt;html&gt;
</pre>

Does not look too special. And it is not.


Using subviews nested templates
-------------------------------

Usually you want to reuse parts of your application. Let's say a body. And a footer.
Freemarker makes that quite simple.

defaultLayout.ftl.html:

<pre class="prettyprint">
&lt;#macro myLayout title=&quot;Layout example&quot;&gt;
&lt;!DOCTYPE html&gt;
&lt;html lang=&quot;en&quot;&gt;
&lt;head&gt;
    &lt;title&gt;${title}&lt;/title&gt;
&lt;body&gt;

    &lt;#nested/&gt;

    &lt;#include &quot;footer.ftl.html&quot;/&gt;

&lt;/body&gt;
&lt;/html&gt;
&lt;/#macro&gt;
</pre>

There are some interesting things:

 * <#macro myLayout title="Layout example"> defines a variable $title a default tile for it
 * a variable in Freemarker looks like 
 * Nesting other views into it is done by <#nested/>
 * And simply including another view is done by <#include "footer.ftl.html"/>
 
footer.ftl.html looks like:

<pre class="prettyprint">
&lt;hr&gt;
&lt;footer&gt;
    &lt;p&gt;Company 2017&lt;/p&gt;
&lt;/footer&gt;
</pre>
... nothing special here...


And the main view "basic.ftl.html" then looks like:

<pre class="prettyprint">
&lt;#import &quot;defaultLayout.ftl.html&quot; as layout&gt; 
&lt;@layout.myLayout &quot;Home page&quot;&gt;    

    &lt;h1&gt;my text&lt;/h1&gt;

&lt;/@layout.myLayout&gt;
</pre>

There are some interesting things:

 * We import our main template <#import "defaultLayout.ftl.html" as layout>
 * We also redefine the default title to "Home Page"
 * The rest of the template will be included where <#nested/> in the defaultLayout.ftl.html is written.
 
When you render things you would call Results.html().template("basic.ftl.html") in your controller method.
 
And that's all.