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

    @FilterWith(AuthenticityFilter.class)
    public Result unauthorized() {
        return Results.html();
    }
}