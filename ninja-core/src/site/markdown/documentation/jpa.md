JPA
===

JPA is the de-facto standard for persistence in Java and Ninja provides out-of-the box support for JPA.


Quickstart
==========

We prepared an archetype to get you up and running. Simply execute:

<pre class="prettyprint">
mvn archetype:generate -DarchetypeGroupId=org.ninjaframework -DarchetypeArtifactId=ninja-jpa-demo-archetype
</pre>

and hit

<pre class="prettyprint">
jetty:run
</pre>

(well - following this chapter will help to understand what is going on...)


Configuration
=============

Two things are important when it comes to configuring JPA.

 * Setting a persistence unit at your application.conf
 * The META-INF/persistence.xml
 
At your application.conf you can set a variable called <code>ninja.jpa.persistence_unit_name</code>

<pre class="prettyprint">
ninja.jpa.persistence_unit_name=mypersistenceunit
</pre>

This tells Ninja what persistence unit to select from persitence.xml. You can (and should) of course
take advantage of using different persistence units for different modes.

<pre class="prettyprint">
ninja.jpa.persistence_unit_name=dev_unit
%test.ninja.jpa.persistence_unit_name=test_unit
%prod.ninja.jpa.persistence_unit_name=prod_unit
</pre>

This causes Ninja to use dev_unit in dev, test_unit ind dev and prod_unit in prod. You can then use for instance
a fast in memory db for testing, a regular postgresql database for development and a highly tuned 
connectionpooled postgresql in production. All of them with differen connection strings of course.


To make that work you have to configure the second component - the persistence.xml which will roughly look like:

<pre class="prettyprint">
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;

&lt;persistence xmlns=&quot;http://java.sun.com/xml/ns/persistence&quot;
    xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
    xsi:schemaLocation=&quot;http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd&quot;
    version=&quot;2.0&quot;&gt;

    &lt;!-- An in memory database useful when running tests. --&gt;
    &lt;persistence-unit name=&quot;test_unit&quot; transaction-type=&quot;RESOURCE_LOCAL&quot;&gt;
        &lt;provider&gt;org.hibernate.ejb.HibernatePersistence&lt;/provider&gt;
        &lt;properties&gt;
            &lt;property name=&quot;javax.persistence.provider&quot; value=&quot;org.hibernate.ejb.HibernatePersistence&quot; /&gt;
            &lt;property name=&quot;hibernate.connection.username&quot; value=&quot;sa&quot; /&gt;
            &lt;property name=&quot;hibernate.connection.password&quot; value=&quot;&quot; /&gt;
            &lt;property name=&quot;hibernate.connection.driver_class&quot; value=&quot;org.hsqldb.jdbcDriver&quot; /&gt;
            &lt;property name=&quot;hibernate.connection.url&quot; value=&quot;jdbc:hsqldb:mem:.&quot; /&gt;
            &lt;property name=&quot;hibernate.dialect&quot; value=&quot;org.hibernate.dialect.HSQLDialect&quot; /&gt;
            &lt;property name=&quot;hibernate.hbm2ddl.auto&quot; value=&quot;update&quot; /&gt;
            &lt;property name=&quot;hibernate.show_sql&quot; value=&quot;false&quot; /&gt;
            &lt;property name=&quot;hibernate.format_sql&quot; value=&quot;false&quot; /&gt;
            &lt;!-- vendor-specific properties go here --&gt;
        &lt;/properties&gt;
    &lt;/persistence-unit&gt;

    &lt;!-- A development database that (at best) should be the same as in production --&gt;
    &lt;persistence-unit name=&quot;dev_unit&quot; transaction-type=&quot;RESOURCE_LOCAL&quot;&gt;
        &lt;provider&gt;org.hibernate.ejb.HibernatePersistence&lt;/provider&gt;

        &lt;properties&gt;
            &lt;property name=&quot;hibernate.connection.driver_class&quot; value=&quot;org.postgresql.Driver&quot;/&gt;
            &lt;property name=&quot;hibernate.dialect&quot; value=&quot;org.hibernate.dialect.PostgreSQLDialect&quot; /&gt;
            &lt;property name=&quot;hibernate.connection.username&quot; value=&quot;ra&quot; /&gt;
            &lt;property name=&quot;hibernate.connection.password&quot; value=&quot;&quot; /&gt;
            &lt;property name=&quot;hibernate.connection.url&quot; value=&quot;jdbc:postgresql://localhost:5432/ra&quot; /&gt;
            &lt;property name=&quot;hibernate.hbm2ddl.auto&quot; value=&quot;update&quot; /&gt;
            &lt;property name=&quot;hibernate.show_sql&quot; value=&quot;true&quot; /&gt;
            &lt;property name=&quot;hibernate.format_sql&quot; value=&quot;true&quot; /&gt; 
            
             &lt;!-- Connection Pooling settings --&gt;
            &lt;property name=&quot;hibernate.connection.provider_class&quot;
                value=&quot;org.hibernate.connection.C3P0ConnectionProvider&quot; /&gt;

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
        &lt;provider&gt;org.hibernate.ejb.HibernatePersistence&lt;/provider&gt;

        &lt;properties&gt;
            &lt;property name=&quot;hibernate.connection.driver_class&quot; value=&quot;org.postgresql.Driver&quot;/&gt;
            &lt;property name=&quot;hibernate.dialect&quot; value=&quot;org.hibernate.dialect.PostgreSQLDialect&quot; /&gt;
            &lt;property name=&quot;hibernate.connection.username&quot; value=&quot;ra&quot; /&gt;
            &lt;property name=&quot;hibernate.connection.password&quot; value=&quot;&quot; /&gt;
            &lt;property name=&quot;hibernate.connection.url&quot; value=&quot;jdbc:postgresql://localhost:5432/ra&quot; /&gt;
            &lt;property name=&quot;hibernate.hbm2ddl.auto&quot; value=&quot;update&quot; /&gt;
            &lt;property name=&quot;hibernate.show_sql&quot; value=&quot;true&quot; /&gt;
            &lt;property name=&quot;hibernate.format_sql&quot; value=&quot;true&quot; /&gt; 
            
             &lt;!-- Connection Pooling settings --&gt;
            &lt;property name=&quot;hibernate.connection.provider_class&quot;
                value=&quot;org.hibernate.connection.C3P0ConnectionProvider&quot; /&gt;

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

The file will reside under META-INF/persistence.xml


Models
======

The models by covention should be put under the package "models". A typical model looks like:

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

In essence the model is a Pojo with some annotations. This is already enough to tell JPA where and
what to save.

Please refer to http://docs.oracle.com/javaee/7/tutorial/doc/persistence-intro.htm for an exhaustive coverage on the topic.

Well. We configured the stuff - we know how to write models. But what can we do with the models?


Saving and querying
===================

To be honest. Ninja is just reusing excellent libraries to provide you with JPA. In that case it is
guice and especially guice-persist (https://code.google.com/p/google-guice/wiki/GuicePersist).

Ninja just offers a convenient out of the box configuration and maps the modes to the persistence units.

Let's have a look at a controller that does some querying:

<pre class="prettyprint">

    @Inject 
    Provider<EntityManager> entitiyManagerProvider;
    
    @Transactional
    public Result getIndex() {
                
        EntityManager entityManager = entitiyManagerProvider.get();
            
        Query q = entityManager.createQuery("SELECT x FROM GuestbookEntry x");
        List<GuestbookEntry> guestbookEntries = (List<GuestbookEntry>) q.getResultList();
        
        String postRoute = router.getReverseRoute(ApplicationController.class, "postIndex");
        
        return Results
                .html()
                .render("guestbookEntries", guestbookEntries).
                render("postRoute", postRoute);

        
    }

</pre>

Two things here are important:

 * The injected Provider for an EntityManager
 * The method that is annotated with <code>@Transactional</code>
 
The entity manager is the key component that allows you to update / save and query data based on your models.
But JPA has to open connections, save data, maintain caches - and you'd possibly go crazy if you'd have to
manage that for each controller method yourself. This is what <code>@Transactional</code> is for. Simply annotate
your method with that annotation and guice-persist will handle all boilerplate for you.


Saving is also quite simple:

<pre class="prettyprint">

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


Conclusions
===========

The default way to operate you persistence units is to by using transaction-type=RESOURCE_LOCAL. It gives
you a lot more control and predictability over what is happening and when stuff gets saved. Ninja
works best in that mode.

If you want to know more about JPA please refer to the official docs at: http://docs.oracle.com/javaee/7/tutorial/doc/persistence-intro.htm .




