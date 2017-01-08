/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import models.Article;
import models.ArticleDto;
import models.ArticlesDto;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.SecureFilter;
import ninja.params.PathParam;

import com.google.inject.Inject;

import dao.ArticleDao;
import etc.LoggedInUser;

public class ApiController {

    @Inject
    ArticleDao articleDao;

    public Result getArticlesJson() {

        ArticlesDto articlesDto = articleDao.getAllArticles();

        return Results.json().render(articlesDto);

    }

    public Result getArticlesXml() {

        ArticlesDto articlesDto = articleDao.getAllArticles();

        return Results.xml().render(articlesDto);

    }
    
    public Result getArticleJson(@PathParam("id") Long id) {
    
        Article article = articleDao.getArticle(id);
        
        return Results.json().render(article);
    
    }

    @FilterWith(SecureFilter.class)
    public Result postArticleJson(@LoggedInUser String username,
                                  ArticleDto articleDto) {

        boolean succeeded = articleDao.postArticle(username, articleDto);

        if (!succeeded) {
            return Results.notFound();
        } else {
            return Results.json();
        }

    }

    @FilterWith(SecureFilter.class)
    public Result postArticleXml(@LoggedInUser String username,
                                 ArticleDto articleDto) {

        boolean succeeded = articleDao.postArticle(username, articleDto);

        if (!succeeded) {
            return Results.notFound();
        } else {
            return Results.xml();
        }

    }
}
