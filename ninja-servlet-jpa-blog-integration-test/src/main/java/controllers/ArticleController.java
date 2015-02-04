/**
 * Copyright (C) 2012-2015 the original author or authors.
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
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.SecureFilter;
import ninja.params.PathParam;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import dao.ArticleDao;
import etc.LoggedInUser;

@Singleton
public class ArticleController {
    
    @Inject
    ArticleDao articleDao;

    ///////////////////////////////////////////////////////////////////////////
    // Show article
    ///////////////////////////////////////////////////////////////////////////
    public Result articleShow(@PathParam("id") Long id) {

        Article article = null;

        if (id != null) {

            article = articleDao.getArticle(id);

        }

        return Results.html().render("article", article);

    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Create new article
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result articleNew() {

        return Results.html();

    }

    @FilterWith(SecureFilter.class)
    public Result articleNewPost(@LoggedInUser String username,
                                 Context context,
                                 @JSR303Validation ArticleDto articleDto,
                                 Validation validation) {

        if (validation.hasViolations()) {

            context.getFlashCookie().error("Please correct field.");
            context.getFlashCookie().put("title", articleDto.title);
            context.getFlashCookie().put("content", articleDto.content);

            return Results.redirect("/article/new");

        } else {
            
            articleDao.postArticle(username, articleDto);
            
            context.getFlashCookie().success("New article created.");
            
            return Results.redirect("/");

        }

    }

}
