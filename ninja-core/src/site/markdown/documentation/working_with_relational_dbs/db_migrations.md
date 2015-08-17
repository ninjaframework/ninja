Database Migrations
===================

When you are using relational databases you need to track and organize schema evolutions.

This becomes increasingly important when many people are collaborating on one code base and also
when you want to consistently upgrade your production database.

To that end Ninja integrates the excellent Flyway migration tool (http://flywaydb.org/).


Configuring migrations in Ninja
===============================

Activate migrations by setting the following property:

<pre class="prettyprint">
ninja.migration.run=true
</pre>

Then set the credentials for your database:

<pre class="prettyprint">
db.connection.url=jdbc:postgresql://localhost:5432/ra
db.connection.username=ra
db.connection.password=
</pre>


Setting up Flyway
=================

Flyway itself manages database migration scripts in a directory called <code>src/main/java/db/migration</code>. You should use
the following naming convention: <code>V1\_\_.sql</code> is your first script, <code>V2\_\_.sql</code>
is your second script and so on.

<code>V1__.sql</code> may look like:

<pre class="prettyprint">
-- Just a simple table
create table GuestbookEntry (
    id int8 not null,
    email varchar(255),
    text varchar(255),
    primary key (id)
);

--needed for hibernate running on postgresql
create sequence hibernate_sequence;

</pre>

A migration script is just plain old SQL. It is really important to stress that Flyway's migration
scripts are NOT database vendor independent. 

Therefore it is strongly discouraged to use different databases in test, dev and prod.


Collaboratively working on migrations
=====================================

When two or more developers are working on the same script (let's say V2\_\_.sql) they'll get 
conflicts once they are trying to merge their changes.

But this is intentional and allows to fix problems in V2\_\_.sql.
After resolving the issues and running your tests you can be confident 
that your production upgrade will also work fine.


Migrations in test, dev and prod
================================

In test mode Ninja automatically drops the schema and runs all migrations from scratch. In dev and production 
migration scripts are executed automatically when they are detected. Usually this happens when the 
first deployed Ninja instance connects with your database. Flyway's migrations are "clustersafe". Therefore
nothing will go wrong when you are deploying 20 instances of your application that try to run the evolutions at
the same time.


Flyway migrations versus Hibernate DDL
======================================

Hibernate - the JPA implementation we are using is already providing a DDL (hbm2ddl) feature 
that can manage and create "migrations" for you. 

But Hibernate's hbm2ddl is "just" a diff tool. 
Once your setup gets more complex Hibernate's feature is not enough.
Even the creators of Hibernate strongly discourage the usage in production systems:

**WARNING: We've seen Hibernate users trying to use SchemaUpdate 
to update the schema of a production database automatically. 
This can quickly end in disaster and won't be allowed by your DBA. 
(From the book Java Persistence with Hibernate).**

Our recommendation is to enable Hibernate's DDL for your first initial developments.

Enable that by setting the following properties inside your persistence unit (persistence.xml):

<pre class="prettyprint">
&lt;property name=&quot;hibernate.hbm2ddl.auto&quot; value=&quot;create&quot; /&gt; 
&lt;property name=&quot;hibernate.show_sql&quot; value=&quot;true&quot; /&gt;
&lt;property name=&quot;hibernate.format_sql&quot; value=&quot;true&quot; /&gt;
</pre>

Hint: Try using **create** (always drops data in your db) or **update** (tries to alter existing tables for
new data) for hibernate.hbm2ddl.auto.

Hibernate will then print out nice SQL statements for the creation of your tables, sequences and so on. 

Once your initial prototype is ready copy them to your first migration (eg V1\_\_.sql) 
and deactivate Hibernate's DDL feature. From now on use Ninja's migrations and everything is safe.

More
====

Flyway offers a lot more. For instance Java based migrations, that really facilitate refactoring
of blobs. Check out Flyway's excellent site at: http://flywaydb.org



