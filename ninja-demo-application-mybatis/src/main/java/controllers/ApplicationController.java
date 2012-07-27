package controllers;

import java.util.Map;

import models.CommentMapper;
import models.CommentMapperImpl;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ApplicationController {

	private final CommentMapperImpl commentMapper;

	@Inject
	public ApplicationController(CommentMapperImpl commentMapper) {
		this.commentMapper = commentMapper;

	}

	public Result postComment(Context context, @Param("text") String text,
			@Param("email") String email) {

		System.out.println("text is: " + context.getParameter("text"));
		System.out.println("em is: " + email);

		return Results.notFound();

	}

	public Result listComments(Context context) {

		Map<String, Object> map = Maps.newHashMap();
		map.put("comments", commentMapper.getComments());

		return Results.html().render(map);

	}

}
