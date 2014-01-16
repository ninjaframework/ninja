Sessions and Flash scope
========================

Sessions Intro
--------------

A session is a package of information that always maps to one particular user of your application. 
It is persistent over many connects. For instance - A user logs into your application today 
and comes back tomorrow. Using sessions you will be able to recognize the connects as belonging to the
same user.

Session handling can be achieved in many ways. Php and many Java stacks use a cookie with a "secret" key
that then maps to a session that is stored on a db or filesystem. These approaches are called server side sessions.

Ninja does not do it that way. Instead Ninja uses so called client-side sessions. The cookie itself stores
the information you want to attach to that session. The cool thing is: Scaling Ninja servers becomes
much simpler. You do not need sticky sessions or any filesystem that has to be fast enough to
deliver sessions to your servers. The bad thing is that a Ninja session can only contain very little
information (up to around 4k).

By the way - Ninja did not invent the concept of client side sessions - The praise goes to Rails (afaic). 

More on sessions: (http://en.wikipedia.org/wiki/Session_(computer_science))


<div class="alert alert-info">Ninja sessions currently are not encrypted. Therefore you should not store any 
critical information. Storing a user id, oder username is fine. Storing
credit card information is really bad practise.</div>


Reading a session value
-----------------------



<pre class="prettyprint">


</pre> 


Saving data inside a session
----------------------------



Other operations
----------------




Flash scope
-----------