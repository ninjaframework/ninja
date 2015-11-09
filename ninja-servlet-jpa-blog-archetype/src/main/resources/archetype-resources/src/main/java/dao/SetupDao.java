#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package dao;


import java.util.List;

import com.google.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.Article;
import models.User;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import javax.persistence.FlushModeType;

public class SetupDao {
    
    @Inject
    Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void setup() {
        
        EntityManager entityManager = entityManagerProvider.get();
        
        Query q = entityManager.createQuery("SELECT x FROM User x");
        List<User> users = (List<User>) q.getResultList();

        if (users.isEmpty()) {

            // Create a new user and save it
            User bob = new User("bob@gmail.com", "secret", "Bob");
            entityManager.persist(bob);
            
            // Create a new post
            Article bobPost3 = new Article(bob, "My third post", lipsum);
            entityManager.persist(bobPost3);

            // Create a new post
            Article bobPost2 = new Article(bob, "My second post", lipsum);
            entityManager.persist(bobPost2);
            
            // Create a new post
            Article bobPost1 = new Article(bob, post1Title, post1Content);
            entityManager.persist(bobPost1);
            
            entityManager.setFlushMode(FlushModeType.COMMIT);
            entityManager.flush();
        }

    }
    
    String lipsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit sed nisl sed lorem commodo elementum in a leo. Aliquam erat volutpat. Nulla libero odio, consectetur eget rutrum ac, varius vitae orci. Suspendisse facilisis tempus elit, facilisis ultricies massa condimentum in. Aenean id felis libero. Quisque nisl eros, accumsan eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula.";

    
    public String post1Title = "Hello to the blog example!";
    public String post1Content = 
            "<p>Hi and welcome to the demo of Ninja!</p> "
            + "<p>This example shows how you can use Ninja in the wild. Some things you can learn:</p>"
            + "<ul>"
            + "<li>How to use the templating system (header, footer)</li>"
            + "<li>How to test your application with ease.</li>"
            + "<li>Setting up authentication (login / logout)</li>"
            + "<li>Internationalization (i18n)</li>" 
            + "<li>Static assets / using webjars</li>"
            + "<li>Persisting data</li>"
            + "<li>Beautiful <a href=${symbol_escape}"/article/3${symbol_escape}">html routes</a> for your application</li>"
            + "<li>How to design your restful Api (<a href=${symbol_escape}"/api/bob@gmail.com/articles.json${symbol_escape}">Json</a> and <a href=${symbol_escape}"/api/bob@gmail.com/articles.xml${symbol_escape}">Xml</a>)</li>"
            + "<li>... and much much more.</li>"
            + "</ul>" 
            + "<p>We are always happy to see you on our mailing list! "
            + "Check out <a href=${symbol_escape}"http://www.ninjaframework.org${symbol_escape}">our website for more</a>.</p>";

}
