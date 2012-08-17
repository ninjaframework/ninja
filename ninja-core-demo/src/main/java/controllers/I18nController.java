package controllers;

import ninja.Context;
import ninja.Result;
import ninja.Results;

import com.google.inject.Singleton;

@Singleton
public class I18nController {

    public Result index(Context context) {
        // Only render the page. It contains some language specific strings.
        return Results.html();
    }

}
