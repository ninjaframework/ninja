package controllers;

import java.util.Map;

import models.Comment;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;

@Singleton
public class CommentController {

	private final LocalServiceTestHelper helper;
	 
	public CommentController() {

		// just a test to see if we get the built in in memory datastore up and running...
	    helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	    helper.setUp();
	    
	    ObjectifyService.register(Comment.class);
	}

	public Result postComment(Context context, @Param("text") String text,
			@Param("email") String email) {
		
		Objectify ofy = ObjectifyService.begin();
		
		Comment comment = new Comment();

		comment.text = text;
		comment.email = email;
		ofy.put(comment);

		return Results.redirect("/comments");

	}

	public Result listComments(Context context) {

		Objectify ofy = ObjectifyService.begin();

		Query<Comment> q = ofy.query(Comment.class);

		Map<String, Object> map = Maps.newHashMap();
		
		java.util.List<Comment> comments = Lists.newArrayList();
		for (Comment com : q) {

			comments.add(com);

		}
		map.put("comments", comments);

		return Results.html().render(map);

	}

}
