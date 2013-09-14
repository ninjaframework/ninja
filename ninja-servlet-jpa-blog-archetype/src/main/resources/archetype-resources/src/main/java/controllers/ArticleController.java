#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
