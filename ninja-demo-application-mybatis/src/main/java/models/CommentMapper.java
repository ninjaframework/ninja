package models;

import java.util.List;

import org.apache.ibatis.annotations.Select;


public interface CommentMapper {
	
    @Select("SELECT * FROM comment")
    List<Comment> getComments();

}
