package models;

import java.util.List;

import org.mybatis.guice.transactional.Transactional;

import com.google.inject.Inject;


public class CommentMapperImpl {
	
	@Inject
	CommentMapper commentMapper;

    @Transactional
    public List<Comment> getComments() {
    	return commentMapper.getComments();
    }

}
