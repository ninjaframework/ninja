Security Guide
==============

Session security
----------------

A Ninja session is a hash of key/values, signed but not encrypted. 
That means that as long as your secret is safe, it is not possible for a third-party to forge sessions.

The secret is stored as key <code>application.secret</code> at <code>conf/application.conf.</code>

That way - several servers sharing the same secret can handle any request coming from your users. 
That's the reason why scaling is simple.

<strong>It is very very important to keep the application.secret private.</strong>

Do not commit it in a public repository, and when you install an application written by 
someone else change the secret key to your own. 

When deploying it is also really useful to use an external configuration containing production settings -
and a special application.secret that is not used in regular development. You can point to an alternate
configuration by using a system variable:

<code> ... -Dninja.external.configuration=/mydir/deployment.conf"</code>.


Generating a new secret
-----------------------

You can generate a random new secret in development mode by simply deleting application.secret from
your conf/application.conf file. When you restart your server Ninja will generate a new secret and 
add it to conf/application.conf.