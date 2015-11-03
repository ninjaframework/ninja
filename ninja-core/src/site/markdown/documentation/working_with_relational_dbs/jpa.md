JPA
===

JPA is the de-facto standard for persistence in Java and Ninja provides out-of-the box support for JPA 2.0. 
JPA support is implemented by Hibernate and transaction handling is facilitated by Guice Persist.


Quickstart
==========

We prepared an archetype to get you up and running. Simply execute:

<pre class="prettyprint">
mvn archetype:generate -DarchetypeGroupId=org.ninjaframework -DarchetypeArtifactId=ninja-servlet-jpa-blog-archetype
</pre>

and hit

<pre class="prettyprint">
mvn ninja:run
</pre>


Configuration
=============

Two things are important when it comes to configuring JPA.

 * Setting a database and persistence unit at your application.conf
 * The META-INF/persistence.xml

First of all you have to set your database credentials in application.conf:

You also have to set the database connection string, username and password like so:

<pre class="prettyprint">
db.connection.url=jdbc:postgresql://localhost:5432/ra
db.connection.username=ra
db.connection.password=
</pre>

Of course you can take advantage of Ninja's different modes and specify a different database in test
and in production:

<pre class="prettyprint">
# development database
db.connection.url=jdbc:postgresql://localhost:5432/ra
db.connection.username=ra
db.connection.password=password

# testing database
%test.db.connection.url=jdbc:postgresql://localhost:5432/test
%test.db.connection.username=ra
%test.db.connection.password=password

# production database
%prod.db.connection.url=jdbc:postgresql://myserver:5432/production_db
%prod.db.connection.username=user
%prod.db.connection.password=password
</pre>

 
To activate JPA you have set a variable called <code>ninja.jpa.persistence_unit_name</code>

<pre class="prettyprint">
ninja.jpa.persistence_unit_name=mypersistenceunit
</pre>

This tells Ninja what persistence unit to select from persitence.xml. You can of course
again specify different persistence units for different modes:

<pre class="prettyprint">
ninja.jpa.persistence_unit_name=dev_unit
%test.ninja.jpa.persistence_unit_name=test_unit
%prod.ninja.jpa.persistence_unit_name=prod_unit
</pre>

This causes Ninja to use dev_unit in dev, test_unit in dev and prod_unit in prod. 
You can then use for instance
a db for testing, another regular PostgreSQL database for development and a highly tuned 
connectionpooled PostgreSQL in production. All of them with different connection strings of course.

To make that finally come to live you have to configure the second JPA component 
- a file called <code>META-INF/persistence.xml</code> which can look like:

<pre class="prettyprint">
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;

&lt;persistence xmlns=&quot;http://java.sun.com/xml/ns/persistence&quot;
    xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
    xsi:schemaLocation=&quot;http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd&quot;
    version=&quot;2.0&quot;&gt;

    &lt;!-- Database settings for development and for tests --&gt;
    &lt;persistence-unit name=&quot;dev_unit&quot; transaction-type=&quot;RESOURCE_LOCAL&quot;&gt;
        &lt;provider&gt;org.hibernate.jpa.HibernatePersistenceProvider&lt;/provider&gt;

        &lt;properties&gt;
            &lt;property name=&quot;hibernate.connection.driver_class&quot; value=&quot;org.postgresql.Driver&quot;/&gt;
            &lt;property name=&quot;hibernate.dialect&quot; value=&quot;org.hibernate.dialect.PostgreSQLDialect&quot; /&gt;

            &lt;property name=&quot;hibernate.show_sql&quot; value=&quot;true&quot; /&gt;
            &lt;property name=&quot;hibernate.format_sql&quot; value=&quot;true&quot; /&gt; 
            
            &lt;!-- Connection Pooling settings --&gt;
            &lt;property name=&quot;hibernate.connection.provider_class&quot;
                value=&quot;org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider&quot; /&gt;

            &lt;property name=&quot;hibernate.c3p0.max_size&quot; value=&quot;100&quot; /&gt;
            &lt;property name=&quot;hibernate.c3p0.min_size&quot; value=&quot;0&quot; /&gt;
            &lt;property name=&quot;hibernate.c3p0.acquire_increment&quot; value=&quot;1&quot; /&gt;
            &lt;property name=&quot;hibernate.c3p0.idle_test_period&quot; value=&quot;300&quot; /&gt;
            &lt;property name=&quot;hibernate.c3p0.max_statements&quot; value=&quot;0&quot; /&gt;
            &lt;property name=&quot;hibernate.c3p0.timeout&quot; value=&quot;100&quot; /&gt;      
        &lt;/properties&gt;
    &lt;/persistence-unit&gt;

    &lt;!-- production database - with sensible connect strings optimized for the real servers. --&gt;
    &lt;persistence-unit name=&quot;prod_unit&quot; transaction-type=&quot;RESOURCE_LOCAL&quot;&gt;
        &lt;provider&gt;org.hibernate.jpa.HibernatePersistenceProvider&lt;/provider&gt;

        &lt;properties&gt;
            &lt;property name=&quot;hibernate.connection.driver_class&quot; value=&quot;org.postgresql.Driver&quot;/&gt;
            &lt;property name=&quot;hibernate.dialect&quot; value=&quot;org.hibernate.dialect.PostgreSQLDialect&quot; /&gt;

            &lt;property name=&quot;hibernate.show_sql&quot; value=&quot;false&quot; /&gt;
            &lt;property name=&quot;hibernate.format_sql&quot; value=&quot;false&quot; /&gt; 
            
             &lt;!-- Connection Pooling settings --&gt;
            &lt;property name=&quot;hibernate.connection.provider_class&quot;
                value=&quot;org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider&quot; /&gt;

            &lt;property name=&quot;hibernate.c3p0.max_size&quot; value=&quot;100&quot; /&gt;
            &lt;property name=&quot;hibernate.c3p0.min_size&quot; value=&quot;0&quot; /&gt;
            &lt;property name=&quot;hibernate.c3p0.acquire_increment&quot; value=&quot;1&quot; /&gt;
            &lt;property name=&quot;hibernate.c3p0.idle_test_period&quot; value=&quot;300&quot; /&gt;
            &lt;property name=&quot;hibernate.c3p0.max_statements&quot; value=&quot;0&quot; /&gt;
            &lt;property name=&quot;hibernate.c3p0.timeout&quot; value=&quot;100&quot; /&gt;      
        &lt;/properties&gt;
    &lt;/persistence-unit&gt;
&lt;/persistence&gt;
</pre>

The file will reside under META-INF/persistence.xml.


Models
======

The models by convention should be put under the package "models". A typical model looks like:

<pre class="prettyprint">

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GuestbookEntry {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id;

    private String text;
    private String email;
    
    public GuestbookEntry() {}
    
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}

</pre>

In essence the model is a POJO with some annotations. This is already enough to tell JPA where and
what to save.

Please refer to https://docs.oracle.com/javaee/7/tutorial/persistence-intro.htm 
for an exhaustive coverage of the topic.

Well. We configured the stuff - we know how to write models. But what can we do with the models?


Saving and querying
===================

To be honest. Ninja is just reusing excellent libraries to provide you with JPA. In that case it is
Guice and especially Guice Persist (https://github.com/google/guice/wiki/GuicePersist).

Ninja just offers a convenient out of the box configuration and maps the modes to the persistence units.

Let's have a look at a controller that does some querying:

<pre class="prettyprint">
@Inject 
Provider&lt;EntityManager&gt; entitiyManagerProvider;

@UnitOfWork
public Result getIndex() {

    EntityManager entityManager = entitiyManagerProvider.get();

    Query q = entityManager.createQuery(&quot;SELECT x FROM GuestbookEntry x&quot;);
    List&lt;GuestbookEntry&gt; guestbookEntries = (List&lt;GuestbookEntry&gt;) q.getResultList();

    String postRoute = router.getReverseRoute(ApplicationController.class, &quot;postIndex&quot;);

    return Results
            .html()
            .render(&quot;guestbookEntries&quot;, guestbookEntries).
            render(&quot;postRoute&quot;, postRoute);


}
</pre>

Two things here are important:

 * The injected Provider for an EntityManager
 * The method that is annotated with <code>@UnitOfWork</code>
 
The entity manager is the key component that allows you to update / save and query data based on your models.
But JPA has to open connections, save data, maintain caches - and you'd possibly go crazy if you'd have to
manage that for each controller method yourself. This is what <code>@UnitOfWork</code> is for. Simply annotate
your method with that annotation and Guice Persists will handle all the boilerplate for you.

But <code>@UnitOfWork</code> only handles connections and does not help you with transactions.
This is what <code>@Transactional</code> is for. <code>@Transactional</code> automatically opens and closes
transactions around the annotated method. Make sure you are using
<code>import com.google.inject.persist.Transactional;</code> for <code>@Transactional</code>.

Saving is also straight forward:

<pre class="prettyprint">
import com.google.inject.persist.Transactional;

@Transactional
public Result postIndex(GuestbookEntry guestbookEntry) {

    logger.info("In postRoute");        

    EntityManager entityManager = entitiyManagerProvider.get();

    entityManager.persist(guestbookEntry);


    return Results.redirect(router.getReverseRoute(ApplicationController.class, "getIndex"));

}
</pre>


Saving really is just a call to entityManager.perist(...). It can not get much simpler. 
But again - don't forget to annotate your method with <code>@Transactional</code>.

**Summing up:** 

1. For read-only queries you should use <code>@UnitOfWork</code> (and it may be faster because
there are no transactions started). You can wrap either a controller method or method of 
your service class.
2. For saving / updating and deleting data always use <code>@Transactional</code>. The same
here: you can wrap either a controller method or method of your service class.
3. For several transactions within one HTTP request or scheduler invocation:
 * a. use <code>@UnitOfWork</code> around the controller or service method and use 
<code>@Transactional</code> or programmatic API of the <code>EntityManager</code> to demarcate 
transactions within the same <code>@UnitOfWork</code>
 * b. use <code>@Transactional</code> or programmatic API of the <code>EntityManager</code> to 
demarcate transactions within the same request or scheduler invocation without <code>@UnitOfWork</code>


More
====

The default way to operate you persistence units is by using transaction-type=RESOURCE_LOCAL. It gives
you a lot more control and predictability over what is happening and when stuff gets saved. Ninja
works best in that mode because the framework is responsible for setting up/shutting down the JPA's 
<code>EntityManagerFactory</code> and <code>EntityManagers</code> in oppose to JTA mode where 
transactions and <code>EntityManagers</code> are injected/managed by JEE containers.

If you want to know more about JPA please refer to the official docs at: 
https://docs.oracle.com/javaee/7/tutorial/persistence-intro.htm .

