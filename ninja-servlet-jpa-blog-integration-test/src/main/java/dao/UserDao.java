package dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.User;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import ninja.jpa.UnitOfWork;

public class UserDao {
    
    @Inject
    Provider<EntityManager> entityManagerProvider;
    
    @UnitOfWork
    public boolean isUserAndPasswordValid(String username, String password) {
        
        if (username != null && password != null) {
            
            EntityManager entityManager = entityManagerProvider.get();
            
            Query q = entityManager.createQuery("SELECT x FROM User x WHERE username = :usernameParam");
            User user = (User) q.setParameter("usernameParam", username).getSingleResult();   

            
            if (user != null) {
                
                if (user.password.equals(password)) {

                    return true;
                }
                
            }

        }
        
        return false;
 
    }

}
