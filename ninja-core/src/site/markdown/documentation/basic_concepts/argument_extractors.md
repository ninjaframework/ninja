Argument extractors
===================


Injecting stuff into your controller - method scope
---------------------------------------------------

Ninja can inject a lot of stuff into your methods by default. For instance 
variable parts of routes via <code>@PathParam</code>. 
Or query / form parameters via <code>@Param</code> and <code>@Params</code>.  

Argument extractors allow you to use the very same mechanism 
(annotations of method parameters) to 
inject inject arbitrary things into the method of a controller. 

This allows you to process a request, extract things 
(like a user currently logged in), and provide this via an annotation.

### Built-in Supported Types

- String
- Boolean (boolean)
- Byte (byte)
- Short (short)
- Character (char)
- Integer (int)
- Long (long)
- Float (float)
- Double (double)
- Enum (any)
- Arrays of all these types (e.g. String[], int[], Enum[])

**NOTES:**

Enums do not require anymore a registration in conf.Module. Values are 
converted automatically, ignoring case. Collections and generics are not 
supported for parameter injection. Types must be explicitly declared 
and basic arrays must be used to ensure runtime type-safety.

By default, empty String parameters will be treated as null values. If you 
explicitely need empty strings for empty given parameters, register the 
`ParamParsers.EmptyStringParamParser` in your conf.Module <code>configure()</code> method:

<pre class="prettyprint">
Multibinder&lt;ParamParser&gt; parsersBinder = Multibinder.newSetBinder(binder(), ParamParser.class);
parsersBinder.addBinding().to(ParamParsers.EmptyStringParamParser.class);
</pre>

### Custom Parameter Types

You can also add your own supported type to Ninja, or override and existing one, 
simply by implementing the <code>ParamParser</code> interface and binding it 
with Guice in your conf.Module <code>configure()</code> method:

<pre class="prettyprint">
Multibinder&lt;ParamParser&gt; parsersBinder = Multibinder.newSetBinder(binder(), ParamParser.class);
parsersBinder.addBinding().to(MyParamParser.class);
</pre>
 

How to write an argument extractor
----------------------------------

Let's write an argument extractor that lets us inject the user currently logged in into
our method via an annotation. At the end our controller should look like:

<pre class="prettyprint">
package controllers;

@Singleton
public class ApplicationController {

    public Result index(
            @LoggedInUser String loggedInUser) {

        //do something with the parameters...
    }

}
</pre>

The <code>@LoggedInUser</code> is a so called argument extractor
and allow you to extract arbitrary things out
of the request and re-package them into anything you want. 
<code>@LoggedInUser</code> for instance determines 
the user and injects the username into the field annotated with <code>@LoggedInUser</code>.

Argument extractors consist of two things: A marker interface and an implementation.

First the marker interface:

<pre class="prettyprint">
@WithArgumentExtractor(LoggedInUserExtractor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface LoggedInUser {}
</pre>

... and the argument extractor itself:

<pre class="prettyprint">
public class LoggedInUserExtractor implements ArgumentExtractor&lt;String&gt; {

    @Override
    public String extract(Context context) {
        
        // if we got no session we break:
        if (context.getSession() != null) {
            
            String username = context.getSession().get("username");
            
            return username;
            
        }
        
        return null;
    }

    @Override
    public Class getExtractedType() {
        return String.class;
    }

    @Override
    public String getFieldName() {
        return null;
    }
}
</pre>

As you can see the interface of the argument extractor references 
<code>@WithArgumentExtractor(LoggedInUserExtractor.class)</code>.
<code>LoggedInUserExtractor</code> itself has full access to the context (and the incoming request) 
and will return the object
annotated initially by 
<code>@LoggedInUser</code>. In our example this was <code>@LoggedInUser String loggedInUser</code>.

### Note: Using dependency injection

Currently, there is a bug that causes <code>@Inject</code> fields to not be injected if you use an empty constructor. The workaround is to create a constructor with an injected parameter, such as <code>Context context</code>.

<pre class="prettyprint">
public class LoggedInUserExtractor implements ArgumentExtractor&lt;String&gt; {

    @Inject
    public LoggedInUserExtractor(Context context) {}

    @Inject
    UserDao userDao;
    
    ...
    
}
</pre>
