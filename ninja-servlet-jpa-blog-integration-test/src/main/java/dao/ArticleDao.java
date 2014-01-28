package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import models.Article;
import models.ArticleDto;
import models.ArticlesDto;
import models.User;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import ninja.jpa.UnitOfWork;

public class ArticleDao {
   
    @Inject
    Provider<EntityManager> entitiyManagerProvider;
    
    @UnitOfWork
    public ArticlesDto getAllArticles() {
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        TypedQuery<Article> query= entityManager.createQuery("SELECT x FROM Article x", Article.class);
        List<Article> articles = query.getResultList();        

        ArticlesDto articlesDto = new ArticlesDto();
        articlesDto.articles = articles;
        
        return articlesDto;
        
    }
    
    @UnitOfWork
    public Article getFirstArticleForFrontPage() {
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        Query q = entityManager.createQuery("SELECT x FROM Article x ORDER BY x.postedAt DESC");
        Article article = (Article) q.setMaxResults(1).getSingleResult();      
        
        return article;
        
        
    }
    
    @UnitOfWork
    public List<Article> getOlderArticlesForFrontPage() {
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        Query q = entityManager.createQuery("SELECT x FROM Article x ORDER BY x.postedAt DESC");
        List<Article> articles = (List<Article>) q.setFirstResult(1).setMaxResults(10).getResultList();        
        
        return articles;
        
        
    }
    
    @UnitOfWork
    public Article getArticle(Long id) {
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        Query q = entityManager.createQuery("SELECT x FROM Article x WHERE x.id = :idParam");
        Article article = (Article) q.setParameter("idParam", id).getSingleResult();        
        
        return article;
        
        
    }
    
    /**
     * Returns false if user cannot be found in database.
     */
    @Transactional
    public boolean postArticle(String username, ArticleDto articleDto) {
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        Query query = entityManager.createQuery("SELECT x FROM User x WHERE username = :usernameParam");
        User user = (User) query.setParameter("usernameParam", username).getSingleResult();
        
        if (user == null) {
            return false;
        }
        
        Article article = new Article(user, articleDto.title, articleDto.content);
        entityManager.persist(article);
        
        return true;
        
    }

}
