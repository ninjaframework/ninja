/**
 * Copyright (C) 2012-2014 the original author or authors.
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

package controllers;

import java.util.List;

import models.Article;
import ninja.Result;
import ninja.Results;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import dao.ArticleDao;
import ninja.jpa.UnitOfWork;

public class BugReproductionController {

    @Inject
    ArticleDao articleDao;

    public BugReproductionController() {

    }
    
    /**
     * Having both @UnitOfWork and @Transactional is stupid. But this is exactly
     * what helps to reproduce the bug.
     * 
     * Check out: https://github.com/ninjaframework/ninja/issues/157
     * 
     * Test 1:
     * Order @Transactional then @UnitOfWork
     * 
     */
    @Transactional
    @UnitOfWork
    public Result testAgainstBug157Test1() {

        Article frontPost = articleDao.getFirstArticleForFrontPage();

        List<Article> olderPosts = articleDao.getOlderArticlesForFrontPage();


        return Results.text().render("ok");

    }
    
    
    /**
     * Having both @UnitOfWork and @Transactional is stupid. But this is exactly
     * what helps to reproduce the bug.
     * 
     * Check out: https://github.com/ninjaframework/ninja/issues/157
     * 
     * 
     * Test 2:
     * Order @UnitOfWork then @Transactional
     * 
     */
    @UnitOfWork
    @Transactional
    public Result testAgainstBug157Test2() {

        Article frontPost = articleDao.getFirstArticleForFrontPage();

        List<Article> olderPosts = articleDao.getOlderArticlesForFrontPage();


        return Results.text().render("ok");

    }
        
        
}
