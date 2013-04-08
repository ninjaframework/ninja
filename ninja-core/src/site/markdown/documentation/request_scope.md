Request scope facilities
========================

Http is all about the request. Ninja offers some facilities to handle stuff you have to
do during a request scope. Authentication, and generating objects for a single request are
common examples. The two major players here are:

 * Filters that wrap a context and add / remove stuff from the context.
 * ArgumentExtractors, that allow to inject arbitrary objects into a controller.


WrappedContext in Filters
-------------------------

Filters work in a chained manner. At the end you usually call filterChain.next(context) to continue
the filtering.

This is cool, because it offers you the possibility to replace and partly overwrite the context 
with your own context. You can possibly even extend the Context with your own methods and
functions. A good starting point is class WrappedContext of Ninja.

The following example allows you to inject an arbitrary parameter in the filter chain. This is helpful
if you want to authorize that user by setting (or not setting) the email as parameter.


<pre class="prettyprint">

    public class WrappedContextFilter implements Filter {

        @Override
        public Result filter(FilterChain filterChain, Context context) {
        
            WrappedContext wrappedContext = new WrappedContext(context) {
            
                // Overwrite the getParamter function of the context.
                // This lets you inject parameters in your controller via @Param("username")
                // But also have a look at ArgumentExtractors. Maybe they are a cleaner solution
                // to your problem :)
                public String getParameter(String key) {
                
                    if (key.equals("username")) {
                        //////////////////////////////////////////////////////////////////////////                    
                        // do some stuff like connecting to your database and verifying the user. 
                        //////////////////////////////////////////////////////////////////////////
                        return "MyUserName";
                    
                    } else {
                    
                        return getParameter(key);
                    }
                
                }            
            };
        
            // Simply continue the chain with the wrapped context
            return filterChain.next(wrappedContext);
        }
  
    }
    
</pre>



You can then use a simple @Param("username") to inject the username of your wrapped context into the controller:

<pre class="prettyprint">

    @FilterWith(WrappedContextFilter.class)
    public Result index(@Param("username") String username) {
    
        // Usually you would do some authentication or error handling with
        // username...
        logger.log("username: " + username); //this will be MyUserName

        return Results.html();

    }
    
</pre>    



Argument Extractors
-------------------

Injecting stuff into your controller via 

<pre class="prettyprint">

    public Result index(@Param("username") String username) { ... }
    
</pre>

is really nice, but what about creating your own annotations for custom controller injections? 
Well. This is pretty simple and can be accomplished by Ninja's ArgumentExtractors. @Param is also
just an ArgumentExtractor by the way.

Let's say we want to inject a User object into our controller based on e.g. logged in status. 
First you have to define your own Annotation interface like so:

<pre class="prettyprint">

    @WithArgumentExtractor(LoggedInUserExtractor.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.PARAMETER })
    public @interface LoggedInUser {
        // Just a marker interface
    }
    
</pre>

You can of course also use annotations with values like in @Param. But we want keep things simple for now.
The next step is to write your own ArgumentExtractor for your User object:

<pre class="prettyprint">

    public class LoggedInUserExtractor implements ArgumentExtractor&#60;User&#62; {

        @Override
        public User extract(Context context) {                    
            //////////////////////////////////////////////////////////////////
            // Usually you would now extract stuff from the context
            // and contact your db to check if user is authenticated or so...        
            ///////////////////////////////////////////////////////////////////
            User user = new User();
            user.email = "user@example.com";
            return user;
        }

        @Override
        public Class&#60;User&#62; getExtractedType() {
            return User.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }
    
</pre>

And then you can simply use that extractor as annotation in your controller to inject a user during a request:

<pre class="prettyprint">

    public Result index(@LoggedInUser User user) {
    
        System.out.println("user's email: " + user.email); //will be user@example.com
        // Usually you want to do some more complex logic here...

        return Results.html();

    }
    
</pre>


That's almost everything you have to know about ArgumentMatchers. They really facilitate injection of arbitrary objects into
your controller during a request.

 


