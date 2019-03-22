/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import models.Article;
import models.ArticleDto;
import models.ArticlesDto;
import models.User;
import ninja.jpa.UnitOfWork;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class ArticleDao {
   
    @Inject
    Provider<EntityManager> entitiyManagerProvider;
    
    @UnitOfWork
    public ArticlesDto getAllArticles() {
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        TypedQuery<Article> q = entityManager.createQuery("SELECT x FROM Article x", Article.class);
        List<Article> articles = q.getResultList();        

        ArticlesDto articlesDto = new ArticlesDto();
        articlesDto.articles = articles;
        
        return articlesDto;
        
    }
    
    @UnitOfWork
    public Article getFirstArticleForFrontPage() {
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        TypedQuery<Article> q = entityManager.createQuery("SELECT x FROM Article x ORDER BY x.postedAt DESC", Article.class);
        Article article = q.setMaxResults(1).getSingleResult();      
        
        return article;
        
        
    }
    
    @UnitOfWork
    public List<Article> getOlderArticlesForFrontPage() {
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        TypedQuery<Article> q = entityManager.createQuery("SELECT x FROM Article x ORDER BY x.postedAt DESC", Article.class);
        List<Article> articles = q.setFirstResult(1).setMaxResults(10).getResultList();            
        
        return articles;
        
        
    }
    
    @UnitOfWork
    public Article getArticle(Long id) {
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        TypedQuery<Article> q = entityManager.createQuery("SELECT x FROM Article x WHERE x.id = :idParam", Article.class);
        Article article = q.setParameter("idParam", id).getSingleResult();        
        
        return article;
        
        
    }
    
    /**
     * Returns false if user cannot be found in database.
     */
    @Transactional
    public boolean postArticle(String username, ArticleDto articleDto) {
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        TypedQuery<User> q = entityManager.createQuery("SELECT x FROM User x WHERE username = :usernameParam", User.class);
        User user = q.setParameter("usernameParam", username).getSingleResult();
        
        if (user == null) {
            return false;
        }
        
        Article article = new Article(user, articleDto.title, articleDto.content);
        entityManager.persist(article);
        
        return true;
        
    }

}
