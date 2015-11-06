package controllers;

import javax.inject.Singleton;

import ninja.AuthenticityFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;

@Singleton
public class AuthenticityController {
    
    public Result form() {
        return Results.html();
    }
    
    public Result token() {
        return Results.html();
    }
    
    public Result authenticate() {
        return Results.html();
    }
    
    public Result notauthenticate() {
        return Results.html();
    }

    @FilterWith(AuthenticityFilter.class)
    public Result unauthorized() {
        return Results.html();
    }
    
    @FilterWith(AuthenticityFilter.class)
    public Result authorized() {
        return Results.html();
    }
}