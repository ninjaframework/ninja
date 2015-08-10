Sessions
========

Introduction
------------

A session is a package of information that always maps to one particular user of your application. 
It is persistent over many connects. For instance - A user logs into your application today 
and comes back tomorrow. Using sessions you will be able to recognize the connects as belonging to the
same user.

Session handling can be achieved in many ways. PHP and many Java stacks use a cookie with a "secret" key
that then maps to a session that is stored on a db or filesystem. These approaches are called server side sessions.

Ninja does not do it that way. Instead Ninja uses so called client-side sessions. The cookie itself stores
the information you want to attach to that session. The cool thing is: Scaling Ninja servers becomes
much simpler. You do not need sticky sessions or any filesystem that has to be fast enough to
deliver sessions to your servers. The bad thing is that a Ninja session can only contain very little
information (up to around 4k).

By the way - Ninja did not invent the concept of client side sessions - The praise goes to Rails (afaik). 

More on sessions: (http://en.wikipedia.org/wiki/Session_(computer_science))


<div class="alert alert-info">
Ninja sessions are not encrypted by default. 
Therefore you should not store any 
critical information. Storing a user id, or username is fine. Storing
credit card information is really bad practice. But if you need to include sensitive data for any reasons,
you can encrypt sessions as described in <a href="/documentation/security/getting_started.html">Security</a>
page.
</div>

Reading a session value
-----------------------

You can access the session inside your controller either by specifying
<code>Session</code> as method parameter. Or by injecting a <code>Context</code> and 
accessing the session via <code>context.getSession()</code>.

Accessing individual values is done by calling <code>get(...)</code> on the 
injected <code>Session</code>.

<pre class="prettyprint">
public Result getUserNameFromSession(Session session) {

    String username = session.get("username");

    return Results.html().render(username);

}
</pre> 


Saving data inside a session
----------------------------

You can store data by using method <code>put(key, value)</code> of the 
<code>Session</code> object.

<pre class="prettyprint">
public Result putUserNameToSession(Session session) {

    session.put("username", "kevin");

    return Results.html();

}
</pre>

Clearing the session
--------------------

Session also offers a <code>clear()</code> method that wipes the session.

<pre class="prettyprint">
public Result clearSession(Session session) {

    session.clear();

    return Results.html();

}
</pre>


