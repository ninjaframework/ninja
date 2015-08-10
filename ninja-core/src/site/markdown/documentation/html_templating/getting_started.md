Html templating
===============

Well. HTML is at the core of this Internet thing. Ninja
uses Freemarker as templating engine.

If you want to know all the advanced stuff you can do with Freemarker
please check out their [excellent manual](http://freemarker.org/).


Basic HTML templating
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

By default, Freemarker template files must end with <code>.ftl.html</code>, but an other extension can be defined by setting a property <code>freemarker.suffix</code> in your configuration file.

Using subviews nested templates
-------------------------------

Usually you want to reuse parts of your application. Let's say a body. And a footer.
Freemarker makes that quite simple.

Let's begin with a layout that will be re-used by many views - defaultLayout.ftl.html:

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

<code>&lt;#macro myLayout title=&quot;Layout example&quot;&gt;</code> defines a 
variable $title a default title for it. Nesting other views into it is done by <code>&lt;#nested/&gt;</code>.
And including other templates can be accomplished via <code>&lt;#include &quot;footer.ftl.html&quot;/&gt;</code>.
 
<code>footer.ftl.html</code> looks like:

<pre class="prettyprint">
&lt;hr&gt;
&lt;footer&gt;
    &lt;p&gt;Company 2017&lt;/p&gt;
&lt;/footer&gt;
</pre>

And the main view <code>basic.ftl.html</code> then looks like:

<pre class="prettyprint">
&lt;#import &quot;defaultLayout.ftl.html&quot; as layout&gt; 
&lt;@layout.myLayout &quot;Home page&quot;&gt;    

    &lt;h1&gt;my text&lt;/h1&gt;

&lt;/@layout.myLayout&gt;
</pre>

We import our main template <code>&lt;#import &quot;defaultLayout.ftl.html&quot; 
as layout&gt;</code> and redefine the default title to "Home Page". 
The rest of the template will be included at <code>&lt;#nested/&gt;</code>.
 