Security Guide
==============

Session security
----------------

A Ninja session is a hash of key/values, signed but not encrypted by default(see next section to enable encryption). 
That means that as long as your secret is safe, it is not possible for a third-party to forge sessions.

The secret is stored as key <code>application.secret</code> at <code>conf/application.conf.</code>
It should be a base64 encoded AES key with size 256.

That way - several servers sharing the same secret can handle any request coming from your users. 
That's the reason why scaling is simple.

<strong>It is very very important to keep the application.secret private.</strong>

Do not commit it in a public repository, and when you install an application written by 
someone else change the secret key to your own. 

When deploying it is also really useful to use an external configuration containing production settings -
and a special application.secret that is not used in regular development. You can point to an alternate
configuration by using a system variable:

<code> ... -Dninja.external.configuration=/mydir/deployment.conf"</code>.


Encrypting sessions
-------------------

Setting up Ninja to encrypt sessions is very simple: you have to enable encryption in configuration file,
namely in <code>application.conf</code>, by adding `application.cookie.encryption=true`. That is that simple!

<div class="alert alert-info">
To be able to use strong algorithms like AES with 256-bit keys,
the <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">JCE Unlimited Strength Jurisdiction Policy Files</a>
must be obtained and installed in the JDK/JRE.
</div>


Generating a new session secret
-------------------------------

You can generate a random new secret in development mode by simply deleting application.secret from
your <code>conf/application.conf</code> file. When you restart your server Ninja will generate a new secret and 
add it to <code>conf/application.conf</code>.
